package car.demo.domain.SeoulAPI.service.collector;

import car.demo.domain.SeoulAPI.dto.ParkingLotData;
import car.demo.domain.SeoulAPI.dto.SeoulParkResponse;
import car.demo.domain.SeoulAPI.dto.SeoulParkingResponse;
import car.demo.domain.SeoulAPI.event.ParkingDataCollectedEvent;
import car.demo.domain.SeoulAPI.event.ParkingStaticDataCollectedEvent;
import car.demo.domain.SeoulAPI.service.ParkingDataCollector;
import car.demo.global.constants.Province;
import car.demo.global.constants.SeoulDistrict;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public void collect() {
        log.info("[SeoulCollector] 정적 데이터 수집 시작");
        fetchParkPage(1, 1000, "0")
            .flatMapMany(response -> {
                if (response == null || response.getGetParkInfo() == null) {
                    log.warn("[SeoulCollector] GetParkInfo 응답이 비어있습니다.");
                    return Flux.empty();
                }

                log.info("정적 데이터 수집 시작 - 전체 개수: {}", response.getGetParkInfo().getListTotalCount());
                int totalCount = response.getGetParkInfo().getListTotalCount() != null ?
                    response.getGetParkInfo().getListTotalCount() : 0;

                processResponse(1, 1000, response.getGetParkInfo().getRow(), totalCount, false);

                if (totalCount <= 1000) {
                    return Flux.empty(); // 추가 조회 불필요
                }

                // 2페이지부터 끝까지 추가 조회
                int lastPage = (int) Math.ceil(totalCount / 1000.0);
                return Flux.range(2, lastPage - 1)
                    .flatMap(page -> {
                        int start = (page - 1) * 1000 + 1;
                        int end = page * 1000;
                        return fetchParkPage(start, end, "0")
                            .doOnNext(res -> {
                                if (res != null && res.getGetParkInfo() != null) {
                                    processResponse(start, end, res.getGetParkInfo().getRow(), totalCount, false);
                                }
                            });
                    }, 1); // 페이징 조회의 경우 순차 처리
            })
            .collectList()
            .block();
    }

    private Mono<SeoulParkResponse> fetchParkPage(int start, int end, String status) {
        // {apiKey}/json/GetParkInfo/{start}/{end}/ / /{status}
        return seoulApiClient.get()
            .uri("/{apiKey}/json/GetParkInfo/{start}/{end}/ / /{status}", apiKey, start, end, status)
            .retrieve()
            .bodyToMono(SeoulParkResponse.class)
            .onErrorResume(error -> {
                log.error("[SeoulCollector] GetParkInfo ({}-{}) 수신 실패: {}", start, end, error.getMessage());
                return Mono.empty();
            });
    }

    private Mono<SeoulParkingResponse> fetchParkingPage(int start, int end, String status) {
        // {apiKey}/json/GetParkingInfo/{start}/{end}/ / /{status}
        return seoulApiClient.get()
            .uri("/{apiKey}/json/GetParkingInfo/{start}/{end}/ / /{status}", apiKey, start, end, status)
            .retrieve()
            .bodyToMono(SeoulParkingResponse.class)
            .onErrorResume(error -> {
                log.error("[SeoulCollector] GetParkingInfo ({}-{}) 수신 실패: {}", start, end, error.getMessage());
                return Mono.empty();
            });
    }

    private void processResponse(int start, int end, List<? extends ParkingLotData> rows, int totalCount, boolean isRealtime) {
        if (rows != null) {
            log.info("[SeoulCollector] ({}-{}) 데이터 수신 성공: {}건 (전체: {}건)",
                start, end, rows.size(), totalCount);

            // 구별로 그룹화하여 이벤트 발행
            Map<String, List<ParkingLotData>> grouped = rows.stream()
                .map(r -> (ParkingLotData) r)
                .collect(Collectors.groupingBy(row -> extractDistrict(row.getAddress())));

            grouped.forEach((district, districtRows) -> {
                if (isRealtime) {
                    eventPublisher.publishEvent(new ParkingDataCollectedEvent(Province.SEOUL, district, districtRows));
                } else {
                    eventPublisher.publishEvent(new ParkingStaticDataCollectedEvent(Province.SEOUL, district, districtRows));
                }
            });
        }
    }

    private String extractDistrict(String address) {
        if (address == null || address.isBlank()) {
            return "서울시";
        }
        String[] parts = address.trim().split("\\s+");
        return parts.length > 0 ? parts[0] : "서울시";
    }

    @Override
    public boolean isAutoCollectible() {
        return true;
    }

    @Override
    public Province getProvince() {
        return Province.SEOUL;
    }

    @Scheduled(cron = "0 * * * * *")
    @SchedulerLock(name = "ParkingService_fetchParkingData", lockAtMostFor = "55s", lockAtLeastFor = "50s")
    public void collect_auto() {
        log.info("[SeoulCollector] 실시간 데이터 수집 시작");
        fetchParkingPage(1, 1000, " ")
            .flatMapMany(response -> {
                if (response == null || response.getGetParkingInfo() == null) {
                    log.warn("[SeoulCollector] GetParkingInfo 응답이 비어있습니다.");
                    return Flux.empty();
                }

                int totalCount = response.getGetParkingInfo().getListTotalCount() != null ?
                    response.getGetParkingInfo().getListTotalCount() : 0;

                processResponse(1, 1000, response.getGetParkingInfo().getRow(), totalCount, true);

                if (totalCount <= 1000) {
                    return Flux.empty();
                }

                int lastPage = (int) Math.ceil(totalCount / 1000.0);
                return Flux.range(2, lastPage - 1)
                    .flatMap(page -> {
                        int start = (page - 1) * 1000 + 1;
                        int end = page * 1000;
                        return fetchParkingPage(start, end, " ")
                            .doOnNext(res -> {
                                if (res != null && res.getGetParkingInfo() != null) {
                                    processResponse(start, end, res.getGetParkingInfo().getRow(), totalCount, true);
                                }
                            });
                    }, 1);
            })
            .collectList()
            .block();
    }
}
