package car.demo.domain.SeoulAPI.event;

import car.demo.domain.SeoulAPI.dto.ParkingLotData;
import car.demo.global.constants.Province;
import java.util.List;

public record ParkingStaticDataCollectedEvent(Province province, String district, List<ParkingLotData> rows) {

}
