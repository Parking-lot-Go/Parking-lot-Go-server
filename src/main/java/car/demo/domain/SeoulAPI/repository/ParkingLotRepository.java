package car.demo.domain.SeoulAPI.repository;

import car.demo.domain.SeoulAPI.entity.ParkingLot;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {
  Optional<ParkingLot> findByParkingCode(String parkingCode);

  @Query("SELECT p FROM ParkingLot p LEFT JOIN FETCH p.parkingStatus " +
         "WHERE (:cursor IS NULL OR p.id > :cursor) " +
         "AND (:parkingName IS NULL OR p.parkingName LIKE %:parkingName%) " +
         "AND (:address IS NULL OR p.address LIKE %:address%) " +
         "AND (:district IS NULL OR p.district LIKE %:district%) " +
         "AND (:type IS NULL OR p.statusType  = :type) " +
         "AND p.lat IS NOT NULL AND p.lat <> '' " +
         "ORDER BY p.id ASC")
  List<ParkingLot> findByCursor(
      @Param("cursor") Long cursor,
      @Param("parkingName") String parkingName,
      @Param("address") String address,
      @Param("district") String district,
      @Param("type") String type,
      Pageable pageable);

  @Query("SELECT p FROM ParkingLot p LEFT JOIN FETCH p.parkingStatus " +
         "WHERE (:parkingName IS NULL OR p.parkingName LIKE %:parkingName%) " +
         "AND (:address IS NULL OR p.address LIKE %:address%) " +
         "AND p.lat IS NOT NULL AND p.lat <> ''")
  List<ParkingLot> findByOptionalParkingNameAndAddress(
      @Param("parkingName") String parkingName,
      @Param("address") String address);
}
