package car.demo.domain.SeoulAPI.service.collector;

import car.demo.domain.SeoulAPI.service.ParkingDataCollector;
import car.demo.global.constants.GyeonggiDoDistrict;
import car.demo.global.constants.Province;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class GyeonggiDoParkingDataCollector implements ParkingDataCollector {

    @Qualifier("GyeonggiDo")
    private final WebClient gyeonggiClient;
    private static final String API_URI = "/ParkingPlace";
    @Value("${gyeonggi.api-key}")
    private String apiKey;

    @Override
    public void collect() {
        // TODO: 경기도 공공데이터 API 연동 및 ParkingLotData 매핑 구현
        Flux.fromArray(GyeonggiDoDistrict.values())
                .flatMap(district ->
                        gyeonggiClient.get().uri(uriBuilder ->
                            uriBuilder.path(API_URI)
                                .queryParam("key", apiKey)
                            .queryParam("Type", "json")
                            .queryParam("pIndex", 1)
                            .queryParam("pSize",1000).build())
                            .retrieve()
                            .bodyToMono(String.class)
                            .doOnSuccess(r -> log.info("Response received for district: {}", r))
                            .onErrorResume(error ->{
                                log.error("Error collecting data for district: {}", district, error);
                                return Mono.empty();
                            }),2)
            .collectList()
            .block();


        log.info("[GyeonggiCollector] 수집 로직은 추후 구현 예정입니다.");
    }

    @Override
    public Province getProvince() {
        return Province.GYEONGGI;
    }
}
