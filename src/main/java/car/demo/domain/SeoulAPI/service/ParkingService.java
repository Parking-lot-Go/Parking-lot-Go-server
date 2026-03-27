package car.demo.domain.SeoulAPI.service;

import car.demo.domain.SeoulAPI.dto.CursorResponseDto;
import car.demo.domain.SeoulAPI.dto.ParkingLotResponseDto;
import car.demo.domain.SeoulAPI.dto.ParkingReqData;
import car.demo.domain.SeoulAPI.repository.ParkingLotRepository;
import car.demo.domain.SeoulAPI.entity.ParkingStatusType;
import car.demo.global.utils.GeoUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingService {

  private final ParkingLotRepository parkingLotRepository;
  private final java.util.List<ParkingDataCollector> collectors;


  @Transactional(readOnly = true)
  public CursorResponseDto<ParkingLotResponseDto> getParkingLots(Long cursor, Integer size, String parkingName,
      String address, String district, Double swLat, Double swLng, Double neLat, Double neLng,
      Double centerLat, Double centerLng, Double radiusKm, ParkingStatusType type) {
    int fetchSize = (size == null) ? 1000 : size;

    // 반지름(radiusKm)이 제공된 경우 검색 범위 확장 또는 신규 설정
    if (radiusKm != null) {
      if (centerLat != null && centerLng != null) {
        // 중심 좌표와 반지름 기준
        GeoUtil.BoundingBox box = GeoUtil.calculateBoundingBox(centerLat, centerLng, radiusKm);
        swLat = box.getSwLat();
        swLng = box.getSwLng();
        neLat = box.getNeLat();
        neLng = box.getNeLng();
      } else if (swLat != null && swLng != null && neLat != null && neLng != null) {
        // 기존 Bounding Box 영역을 radiusKm 만큼 확장 (마진 추가)
        double midLat = (swLat + neLat) / 2.0;
        double latMargin = radiusKm / 111.0;
        double lngMargin = radiusKm / (111.0 * Math.cos(Math.toRadians(midLat)));

        swLat -= latMargin;
        neLat += latMargin;
        swLng -= lngMargin;
        neLng += lngMargin;
      }
    }

    // hasNext 확인을 위해 1개 더 조회
    List<ParkingLotResponseDto> results = parkingLotRepository.findByCursor(
            cursor, parkingName, address, district, type, swLat, swLng, neLat, neLng, PageRequest.of(0, fetchSize + 1))
        .stream()
        .map(ParkingLotResponseDto::fromEntity)
        .toList();

    boolean hasNext = results.size() > fetchSize;
    List<ParkingLotResponseDto> content = hasNext ? results.subList(0, fetchSize) : results;

    Long nextCursor = null;
    if (hasNext && !content.isEmpty()) {
      nextCursor = content.get(content.size() - 1).getId();
    }

    return CursorResponseDto.of(content, nextCursor, hasNext);
  }

  public boolean customFetchParkingData(ParkingReqData body) {
    if (body == null || body.province() == null) {
      log.warn("[CustomFetch] 요청된 지역(Province) 정보가 없습니다.");
      return false;
    }

    boolean found = false;
    for (ParkingDataCollector collector : collectors) {
      if (collector.getProvince() == body.province()) {
        found = true;
        try {
          log.info("[CustomFetch] {} 지역 데이터 수집 시작: {}", body.province(), collector.getClass().getSimpleName());
          collector.collect();
          log.info("[CustomFetch] {} 지역 데이터 수집 완료: {}", body.province(), collector.getClass().getSimpleName());
        } catch (Exception e) {
          log.error("[CustomFetch] {} 수집 중 오류 발생: {}", collector.getClass().getSimpleName(), e.getMessage(), e);
          // 필요 시 예외를 다시 던져서 컨트롤러에 500 응답을 줄 수도 있음
        }
      }
    }

    if (!found) {
      log.warn("[CustomFetch] {} 지역을 담당하는 수집기가 등록되어 있지 않습니다.", body.province());
    }
    return found;
  }
}
