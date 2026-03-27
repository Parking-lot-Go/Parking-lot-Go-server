package car.demo.domain.SeoulAPI.service;

import car.demo.domain.SeoulAPI.dto.CursorResponseDto;
import car.demo.domain.SeoulAPI.dto.ParkingLotResponseDto;
import car.demo.domain.SeoulAPI.dto.ParkingReqData;
import car.demo.domain.SeoulAPI.dto.SeoulParkingResponse;
import car.demo.domain.SeoulAPI.event.ParkingDataCollectedEvent;
import car.demo.domain.SeoulAPI.repository.ParkingLotRepository;
import car.demo.global.constants.SeoulDistrict;
import car.demo.domain.SeoulAPI.entity.ParkingStatusType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
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


  @Scheduled(cron = "0 * * * * *")
  @SchedulerLock(name = "ParkingService_fetchParkingData", lockAtMostFor = "55s", lockAtLeastFor = "50s")
  public void fetchParkingData() {
    // 자동 수집 대상으로 등록된 수집기만 실행
    collectors.stream()
        .filter(ParkingDataCollector::isAutoCollectible)
        .forEach(collector -> {
          try {
            collector.collect();
          } catch (Exception e) {
            log.error("[Scheduler] 수집기 실행 실패: {}", collector.getClass().getSimpleName(), e);
          }
        });
  }

  public void customFetchParkingData(ParkingReqData body) {
    // 간단히 전체 수집을 실행. (필터링 수집은 Collector 확장으로 추후 지원 가능)
    fetchParkingData();
  }
}
