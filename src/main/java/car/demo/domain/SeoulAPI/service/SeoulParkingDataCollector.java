package car.demo.domain.SeoulAPI.service;

import car.demo.domain.SeoulAPI.dto.ParkingLotData;
import car.demo.domain.SeoulAPI.dto.SeoulParkingResponse;
import car.demo.domain.SeoulAPI.event.ParkingDataCollectedEvent;
import car.demo.global.constants.SeoulDistrict;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeoulParkingDataCollector implements ParkingDataCollector {

    @Qualifier("seoulApiClient")
    private final WebClient seoulApiClient;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${seoul.api-key}")
    private String apiKey;

    @Override
    public boolean isAutoCollectible() {
        return true;
    }

    @Override
    public void collect() {
        Flux.fromArray(SeoulDistrict.values())
            .flatMap(district -> seoulApiClient.get()
                .uri("/{apiKey}/json/GetParkingInfo/1/1000/{district}", apiKey, district.getKoreanName())
                .retrieve()
                .bodyToMono(SeoulParkingResponse.class)
                .doOnSuccess(response -> {
                    if (response != null && response.getGetParkingInfo() != null) {
                        List<ParkingLotData> rows = response.getGetParkingInfo().getRow()
                            .stream().map(r -> (ParkingLotData) r).toList();
                        eventPublisher.publishEvent(new ParkingDataCollectedEvent(
                            district.getKoreanName(),
                            rows
                        ));
                    }
                })
                .onErrorResume(error -> {
                    log.error("[SeoulCollector] {} 수신 실패: {}", district.getKoreanName(), error.getMessage());
                    return Mono.empty();
                }), 5)
            .collectList()
            .block();
    }
}
