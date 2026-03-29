package car.demo.domain.search.service.impl;

import car.demo.domain.SeoulAPI.dto.CursorResponseDto;
import car.demo.domain.SeoulAPI.dto.ParkingLotResponseDto;
import car.demo.domain.SeoulAPI.entity.ParkingStatusType;
import car.demo.domain.SeoulAPI.service.ParkingService;
import car.demo.domain.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

  private final ParkingService parkingService;

  @Override
  public CursorResponseDto<ParkingLotResponseDto> searchNear(Double lat, Double lng, Double radius) {
    // 기본적으로 2km 반경으로 검색 (필요에 따라 조절)
    Double searchRadius = (radius == null) ? 2.0 : radius;
    return parkingService.getParkingLots(null, 10, null, null, null, null, null, null, null, lat, lng, searchRadius, ParkingStatusType.NOT_LINKED);
  }
}
