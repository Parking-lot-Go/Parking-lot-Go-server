package car.demo.domain.SeoulAPI.service.collector;

import car.demo.domain.SeoulAPI.dto.GyeonggiDoParkingResponse;
import car.demo.domain.SeoulAPI.dto.ParkingLotData;
import car.demo.domain.SeoulAPI.event.ParkingDataCollectedEvent;
import car.demo.domain.SeoulAPI.service.ParkingDataCollector;
import car.demo.global.constants.GyeonggiDoDistrict;
import car.demo.global.constants.Province;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class GyeonggiDoParkingDataCollector implements ParkingDataCollector {

    @Qualifier("GyeonggiDo")
    private final WebClient gyeonggiClient;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    private static final String API_URI = "/ParkingPlace";
    @Value("${gyeonggi.api-key}")
    private String apiKey;

    @Override
    public void collect() {
        Flux.fromArray(GyeonggiDoDistrict.values())
                .flatMap(district ->
                        gyeonggiClient.get().uri(uriBuilder ->
                            uriBuilder.path(API_URI)
                                .queryParam("KEY", apiKey)
                            .queryParam("Type", "json")
                            .queryParam("pIndex", 1)
                            .queryParam("pSize",1000)
                            .queryParam("SIGUN_NM", district.getKoreanName())
                            .build())
                            .retrieve()
                            .bodyToMono(String.class)
                            .flatMap(responseStr -> {
                                try {
                                    GyeonggiDoParkingResponse response = objectMapper.readValue(responseStr, GyeonggiDoParkingResponse.class);
                                    if (response != null && response.getParkingPlace() != null && response.getParkingPlace().size() > 1) {
                                        List<ParkingLotData> rows = response.getParkingPlace().get(1).getRow()
                                            .stream().map(r -> (ParkingLotData) r).toList();
                                        eventPublisher.publishEvent(new ParkingDataCollectedEvent(
                                            Province.GYEONGGI,
                                            district.getKoreanName(),
                                            rows
                                        ));
                                        log.info("[GyeonggiCollector] {} 데이터 수집 성공: {} 건", district.getKoreanName(), rows.size());
                                    }
                                    return Mono.empty();
                                } catch (Exception e) {
                                    log.error("[GyeonggiCollector] {} 파싱 실패. 응답: {}, 에러: {}", district.getKoreanName(), responseStr, e.getMessage());
                                    return Mono.empty();
                                }
                            })
                            .onErrorResume(error ->{
                                log.error("[GyeonggiCollector] {} 수신 실패: {}", district.getKoreanName(), error.getMessage());
                                return Mono.empty();
                            }),2)
            .collectList()
            .block();
    }

    @Override
    public Province getProvince() {
        return Province.GYEONGGI;
    }
}
