package car.demo.domain.favorite.entity;

import car.demo.domain.SeoulAPI.entity.ParkingLot;
import car.demo.domain.user.entity.User;
import car.demo.global.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "favorite_parking_lot",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"user_id", "parking_lot_id"})
       })
public class FavoriteParkingLot extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_lot_id", nullable = false)
    private ParkingLot parkingLot;

    @Builder
    public FavoriteParkingLot(User user, ParkingLot parkingLot) {
        this.user = user;
        this.parkingLot = parkingLot;
    }
}
