package car.demo.domain.SeoulAPI.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "parking_status")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class ParkingStatus {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parking_lot_id", nullable = false)
  private ParkingLot parkingLot;

  public void setParkingLot(ParkingLot parkingLot) {
    this.parkingLot = parkingLot;
  }

  private Integer totalCapacity;    // TPKCT (총 주차면)
  private Integer currentCount;     // NOW_PRK_VHCL_CNT (현재 주차 차량수)
  private Integer availableCount;   // totalCapacity - currentCount (여유 주차면)
  private LocalDateTime updatedAt;  // NOW_PRK_VHCL_UPDT_TM (현재 주차 차량수 업데이트시간)

  public void update(int totalCapacity, int currentCount, LocalDateTime updatedAt) {
    this.totalCapacity = totalCapacity;
    this.currentCount = Math.max(0, currentCount);
    this.availableCount = Math.max(0, totalCapacity - this.currentCount);
    this.updatedAt = updatedAt;
  }
}
