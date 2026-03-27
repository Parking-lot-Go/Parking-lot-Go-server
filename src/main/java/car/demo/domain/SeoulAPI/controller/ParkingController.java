package car.demo.domain.SeoulAPI.controller;

import car.demo.domain.SeoulAPI.dto.CursorResponseDto;
import car.demo.domain.SeoulAPI.dto.ParkingLotResponseDto;
import car.demo.domain.SeoulAPI.entity.ParkingStatusType;
import car.demo.domain.SeoulAPI.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/parking")
@RequiredArgsConstructor
public class ParkingController {

  private final ParkingService parkingService;

  @GetMapping
  public ResponseEntity<CursorResponseDto<ParkingLotResponseDto>> getParkingLots(
      @RequestParam(name = "cursor", required = false) Long cursor,
      @RequestParam(name = "size", required = false) Integer size,
      @RequestParam(name = "name", required = false) String name,
      @RequestParam(name = "address", required = false) String address,
      @RequestParam(name = "district", required = false) String district,
      @RequestParam(name = "type" ,required = false, defaultValue = "REALTIME") ParkingStatusType type) {
    return ResponseEntity.ok(parkingService.getParkingLots(cursor, size, name, address, district,type));
  }

}
