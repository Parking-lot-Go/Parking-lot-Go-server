package car.demo.domain.SeoulAPI.event;

import car.demo.domain.SeoulAPI.dto.ParkingLotData;
import java.util.List;

public record ParkingDataCollectedEvent(String district, List<ParkingLotData> rows) {

}
