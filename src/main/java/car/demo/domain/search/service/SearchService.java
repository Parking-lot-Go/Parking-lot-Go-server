package car.demo.domain.search.service;

import car.demo.domain.SeoulAPI.dto.CursorResponseDto;
import car.demo.domain.SeoulAPI.dto.ParkingLotResponseDto;

public interface SearchService {

  CursorResponseDto<ParkingLotResponseDto> searchNear(Double lat, Double lng, Double radius);
}
