package car.demo.domain.SeoulAPI.repository;

import car.demo.domain.SeoulAPI.entity.ParkingStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingStatusRepository extends JpaRepository<ParkingStatus, Long> {
  Optional<ParkingStatus> findByParkingLot_ParkingCode(String parkingCode);
}
