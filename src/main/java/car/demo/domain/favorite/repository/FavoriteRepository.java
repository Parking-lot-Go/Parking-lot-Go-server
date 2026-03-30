package car.demo.domain.favorite.repository;

import car.demo.domain.favorite.entity.FavoriteParkingLot;
import car.demo.domain.user.entity.User;
import car.demo.domain.SeoulAPI.entity.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<FavoriteParkingLot, Long> {
    @Query("SELECT f FROM FavoriteParkingLot f " +
           "JOIN FETCH f.parkingLot p " +
           "LEFT JOIN FETCH p.parkingStatus " +
           "WHERE f.user = :user " +
           "AND (:cursor IS NULL OR f.id > :cursor) " +
           "ORDER BY f.id ASC")
    List<FavoriteParkingLot> findAllByUserWithCursor(@Param("user") User user, @Param("cursor") Long cursor, Pageable pageable);

    List<FavoriteParkingLot> findAllByUser(User user);
    Optional<FavoriteParkingLot> findByUserAndParkingLot(User user, ParkingLot parkingLot);
    boolean existsByUserAndParkingLot(User user, ParkingLot parkingLot);
}
