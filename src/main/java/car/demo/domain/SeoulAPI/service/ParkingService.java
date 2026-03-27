package car.demo.domain.SeoulAPI.service;

import car.demo.domain.SeoulAPI.dto.CursorResponseDto;
import car.demo.domain.SeoulAPI.dto.ParkingLotResponseDto;
import car.demo.domain.SeoulAPI.dto.ParkingReqData;
import car.demo.domain.SeoulAPI.repository.ParkingLotRepository;
import car.demo.domain.SeoulAPI.entity.ParkingStatusType;
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
  public CursorResponseDto<ParkingLotResponseDto> getParkingLots(Long cursor, Integer size, String parkingName, String address,String district,ParkingStatusType type) {
    int fetchSize = (size == null) ? 1000 : size;

    // hasNext 확인을 위해 1개 더 조회
    List<ParkingLotResponseDto> results = parkingLotRepository.findByCursor(
            cursor, parkingName, address, district, type,PageRequest.of(0, fetchSize + 1))
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
