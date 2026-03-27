package car.demo.domain.SeoulAPI.entity;

import car.demo.domain.SeoulAPI.dto.SeoulParkingResponse;
import car.demo.global.utils.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "parking_lot")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class ParkingLot extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String parkingCode;       // PKLT_CD

  @Column(nullable = false)
  private String parkingName;       // PKLT_NM

  private String address;           // ADDR
  private String district;          // 구 이름 (강북구 등)
  private String parkingType;       // PKLT_TYPE
  private String parkingTypeName;   // PRK_TYPE_NM
  private String operationType;     // OPER_SE_NM
  private String phone;             // TELNO

  private Integer totalCapacity;    // TPKCT

  // 운영 시간
  private String weekdayStart;      // WD_OPER_BGNG_TM
  private String weekdayEnd;        // WD_OPER_END_TM
  private String weekendStart;      // WE_OPER_BGNG_TM
  private String weekendEnd;        // WE_OPER_END_TM
  private String holidayStart;      // LHLDY_OPER_BGNG_TM
  private String holidayEnd;        // LHLDY_OPER_END_TM

  // 요금 정보
  private String feeType;           // PAY_YN_NM
  private Integer basicFee;         // BSC_PRK_CRG
  private Integer basicTime;        // BSC_PRK_HR
  private Integer additionalFee;    // ADD_PRK_CRG
  private Integer additionalTime;   // ADD_PRK_HR
  private Integer monthlyFee;       // PRD_AMT
  private Integer dayMaxFee;        // DAY_MAX_CRG

  private String lat; // y좌표 (Latitude)
  private String lng; // x좌표 (Longitude)

  // 실시간 연계 여부 (핵심!)
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ParkingStatusType statusType; // PRK_STTS_YN

  // 1:1 관계
  @OneToOne(mappedBy = "parkingLot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private ParkingStatus parkingStatus;

  public void addParkingStatus(ParkingStatus parkingStatus) {
    this.parkingStatus = parkingStatus;
    parkingStatus.setParkingLot(this);
  }

  public static ParkingLot from(SeoulParkingResponse.ParkingRow row, String district) {
    int totalCapacity = safeInt(row.getTotalParkingCapacity());
    int currentCount = Math.max(0, safeInt(row.getCurrentParkingCount()));
    int availableCount = Math.max(0, totalCapacity - currentCount);

    ParkingLot lot = ParkingLot.builder()
        .parkingCode(row.getParkingCode())
        .parkingName(row.getParkingName())
        .address(row.getAddress())
        .district(district)
        .parkingType(row.getParkingType())
        .parkingTypeName(row.getParkingTypeName())
        .operationType(row.getOperationSelectionName())
        .phone(row.getTelNo())
        .totalCapacity(totalCapacity)
        .weekdayStart(row.getWeekdayBeginTime())
        .weekdayEnd(row.getWeekdayEndTime())
        .weekendStart(row.getWeekendBeginTime())
        .weekendEnd(row.getWeekendEndTime())
        .holidayStart(row.getHolidayBeginTime())
        .holidayEnd(row.getHolidayEndTime())
        .feeType(row.getPayYnName())
        .basicFee(safeInt(row.getBasicParkingCharge()))
        .basicTime(safeInt(row.getBasicParkingTime()))
        .additionalFee(safeInt(row.getAddParkingCharge()))
        .additionalTime(safeInt(row.getAddParkingTime()))
        .monthlyFee(parseAmount(row.getPeriodicTicketAmount()))
        .dayMaxFee(safeInt(row.getDayMaxCharge()))
        .lat(row.getLat())
        .lng(row.getLot())
        .statusType(ParkingStatusType.from(row.getParkingStatusYn()))
        .build();

    // 초기 상태 생성 (TPKCT, NOW_PRK_VHCL_CNT, NOW_PRK_VHCL_UPDT_TM 기반)
    lot.addParkingStatus(ParkingStatus.builder()
        .totalCapacity(totalCapacity)
        .currentCount(currentCount)
        .availableCount(availableCount)
        .updatedAt(parseDateTime(row.getCurrentParkingUpdateTime()))
        .build());

    return lot;
  }

  public void update(SeoulParkingResponse.ParkingRow row) {
    this.parkingName = row.getParkingName();
    this.address = row.getAddress();
    this.parkingType = row.getParkingType();
    this.parkingTypeName = row.getParkingTypeName();
    this.operationType = row.getOperationSelectionName();
    this.phone = row.getTelNo();
    this.totalCapacity = safeInt(row.getTotalParkingCapacity());
    this.weekdayStart = row.getWeekdayBeginTime();
    this.weekdayEnd = row.getWeekdayEndTime();
    this.weekendStart = row.getWeekendBeginTime();
    this.weekendEnd = row.getWeekendEndTime();
    this.holidayStart = row.getHolidayBeginTime();
    this.holidayEnd = row.getHolidayEndTime();
    this.feeType = row.getPayYnName();
    this.basicFee = safeInt(row.getBasicParkingCharge());
    this.basicTime = safeInt(row.getBasicParkingTime());
    this.additionalFee = safeInt(row.getAddParkingCharge());
    this.additionalTime = safeInt(row.getAddParkingTime());
    this.monthlyFee = parseAmount(row.getPeriodicTicketAmount());
    this.dayMaxFee = safeInt(row.getDayMaxCharge());
    this.statusType = ParkingStatusType.from(row.getParkingStatusYn());

    // 실시간 상태 업데이트 (TPKCT, NOW_PRK_VHCL_CNT, NOW_PRK_VHCL_UPDT_TM)
    if (this.parkingStatus != null) {
      this.parkingStatus.update(
          this.totalCapacity,
          safeInt(row.getCurrentParkingCount()),
          parseDateTime(row.getCurrentParkingUpdateTime())
      );
    }
  }

  private static LocalDateTime parseDateTime(String dateTimeStr) {
    if (dateTimeStr == null || dateTimeStr.isEmpty()) {
      return LocalDateTime.now();
    }
    try {
      // 서울시 API 포맷: "2026-03-27 15:19:51"
      return LocalDateTime.parse(dateTimeStr.replace(" ", "T"));
    } catch (Exception e) {
      return LocalDateTime.now();
    }
  }

  private static int safeInt(Double value) {
    return value != null ? value.intValue() : 0;
  }

  private static Integer parseAmount(String amount) {
    if (amount == null || amount.isEmpty() || "null".equals(amount)) {
      return 0;
    }
    try {
      return Integer.parseInt(amount);
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  public boolean isRealtime() {
    return this.statusType == ParkingStatusType.REALTIME;
  }
}
