package car.demo.domain.SeoulAPI.service;

import car.demo.domain.SeoulAPI.dto.CursorResponseDto;
import car.demo.domain.SeoulAPI.dto.ParkingLotResponseDto;
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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingService {

  @Qualifier("seoulApiClient")
  private final WebClient seoulApiClient;

  private final ApplicationEventPublisher eventPublisher;
  private final ParkingLotRepository parkingLotRepository;

  @Value("${seoul.api-key}")
  private String API_KEY;


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
//    log.info("[Scheduler] 주차 데이터 수집 시작");

    Flux.fromArray(SeoulDistrict.values())
        .flatMap(district -> seoulApiClient.get()
            .uri("/{apiKey}/json/GetParkingInfo/1/1000/{district}", API_KEY, district.getKoreanName())
            .retrieve()
            .bodyToMono(SeoulParkingResponse.class)
            .doOnSuccess(response -> {
              if (response != null && response.getGetParkingInfo() != null) {

                // 이벤트 발행
                eventPublisher.publishEvent(new ParkingDataCollectedEvent(
                    district.getKoreanName(),
                    response.getGetParkingInfo().getRow()
                ));
              }
            })
            .onErrorResume(error -> {
              log.error("[Scheduler] {} 수신 실패: {}", district.getKoreanName(), error.getMessage());
              return Mono.empty();
            }), 5)
        .collectList()
        .block();

//    log.info("[Scheduler] 전체 주차 데이터 수집 완료");
  }

}
