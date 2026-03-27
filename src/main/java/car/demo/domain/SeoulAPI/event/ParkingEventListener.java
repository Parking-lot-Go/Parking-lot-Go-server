package car.demo.domain.SeoulAPI.event;

import car.demo.domain.SeoulAPI.dto.ParkingLotData;
import car.demo.domain.SeoulAPI.entity.ParkingLot;
import car.demo.domain.SeoulAPI.repository.ParkingLotRepository;
import car.demo.domain.SeoulAPI.repository.ParkingStatusRepository;
import car.demo.global.constants.Province;
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
    event.rows().forEach(row -> {
      upsertParkingLot(row, event.district(), event.province()); // 지역(Province) 정보 추가 전달
    });
  }

  @EventListener
  @Async
  @Transactional
  public void handleParkingStaticDataCollected(ParkingStaticDataCollectedEvent event) {
    event.rows().forEach(row -> {
      upsertStaticParkingLot(row, event.district(), event.province());
    });
  }

  private void upsertParkingLot(ParkingLotData row, String district, Province province) {
    parkingLotRepository.findByParkingCode(row.getParkingCode())
        .ifPresentOrElse(
            lot -> lot.update(row, province),   // 이미 있으면 업데이트 (서울인 경우만 상태 포함)
            () -> parkingLotRepository.save(ParkingLot.from(row, district, province)) // 없으면 저장 (서울인 경우만 상태 포함)
        );
  }

  private void upsertStaticParkingLot(ParkingLotData row, String district, Province province) {
    parkingLotRepository.findByParkingCode(row.getParkingCode())
        .ifPresentOrElse(
            lot -> lot.updateStatic(row),   // 정적 데이터만 업데이트
            () -> parkingLotRepository.save(ParkingLot.fromStatic(row, district, province)) // 정적 데이터로 저장
        );
  }
}
