package car.demo.domain.SeoulAPI.event;

import car.demo.domain.SeoulAPI.dto.SeoulParkingResponse;
import car.demo.domain.SeoulAPI.entity.ParkingLot;
import car.demo.domain.SeoulAPI.repository.ParkingLotRepository;
import car.demo.domain.SeoulAPI.repository.ParkingStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ParkingEventListener {

  private final ParkingLotRepository parkingLotRepository;
  private final ParkingStatusRepository parkingStatusRepository;

  @EventListener
  @Async // 비동기 처리 (수집 스레드 블로킹 방지)
  @Transactional
  public void handleParkingDataCollected(ParkingDataCollectedEvent event) {
//    log.info("[Event] {} 저장 시작 - {} 건", event.district(), event.rows().size());

    event.rows().forEach(row -> {
      upsertParkingLot(row, event.district()); // 기본 정보 및 상태 통합 upsert
    });
  }

  private void upsertParkingLot(SeoulParkingResponse.ParkingRow row, String district) {
    parkingLotRepository.findByParkingCode(row.getParkingCode())
        .ifPresentOrElse(
            lot -> lot.update(row),   // 이미 있으면 업데이트 (상태 포함)
            () -> parkingLotRepository.save(ParkingLot.from(row, district)) // 없으면 저장 (상태 포함)
        );
  }
}
