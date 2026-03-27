package car.demo.domain.SeoulAPI.event;

import car.demo.domain.SeoulAPI.dto.SeoulParkingResponse;
import car.demo.domain.SeoulAPI.dto.SeoulParkingResponse.ParkingRow;
import java.util.List;

public record ParkingDataCollectedEvent(String district, List<ParkingRow> rows) {

}
