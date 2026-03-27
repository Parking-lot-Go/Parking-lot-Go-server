package car.demo.domain.SeoulAPI.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class GyeonggiDoParkingDataCollector implements ParkingDataCollector {

    @Qualifier("GyeonggiDo")
    private final WebClient gyeonggiClient;

    @Override
    public void collect() {
        // TODO: 경기도 공공데이터 API 연동 및 ParkingLotData 매핑 구현
        log.info("[GyeonggiCollector] 수집 로직은 추후 구현 예정입니다.");
    }
}
