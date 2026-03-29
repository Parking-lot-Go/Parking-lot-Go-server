package car.demo.domain.search.controller;

import car.demo.domain.SeoulAPI.dto.CursorResponseDto;
import car.demo.domain.SeoulAPI.dto.ParkingLotResponseDto;
import car.demo.domain.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

  private final SearchService searchService;


  @GetMapping
  public ResponseEntity<CursorResponseDto<ParkingLotResponseDto>> search(
      @RequestParam(name = "type", required = false) String type,
      @RequestParam(name = "lat", required = false) Double lat,
      @RequestParam(name = "lng", required = false) Double lng,
      @RequestParam(name = "radius", required = false) Double radius,
      @RequestParam(name = "query", required = false) String query) {

    if ("near".equalsIgnoreCase(type) && lat != null && lng != null) {
      log.info("여기로죄?");
      return ResponseEntity.ok(searchService.searchNear(lat, lng, radius));
    }

    // 기존 검색 로직 (현재는 단순히 빈 결과 또는 query 처리)
    return ResponseEntity.ok(CursorResponseDto.of(java.util.Collections.emptyList(), null, false));
  }

}
