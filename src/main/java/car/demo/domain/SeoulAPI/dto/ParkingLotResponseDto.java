package car.demo.domain.SeoulAPI.dto;

import car.demo.domain.SeoulAPI.entity.ParkingLot;
import car.demo.domain.SeoulAPI.entity.ParkingStatus;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@Getter
@Builder
@AllArgsConstructor
public class ParkingLotResponseDto {

  private Long id;
  private String parkingCode;
  private String parkingName;
  private String address;
  private String district;
  private String parkingTypeName;
  private String operationType;
  private String phone;
  private Integer totalCapacity;
  private Integer currentCount;
  private Integer availableCount;
  private String updatedAt;
  private String feeType;
  private Integer basicFee;
  private Integer basicTime;
  private Integer additionalFee;
  private Integer additionalTime;
  private Integer monthlyFee;
  private Integer dayMaxFee;
  private Double lat;
  private Double lng;
  private String weekdayStart;
  private String weekdayEnd;
  private String weekendStart;
  private String weekendEnd;
  private String holidayStart;
  private String holidayEnd;

  public static ParkingLotResponseDto fromEntity(ParkingLot entity) {
    ParkingStatus status = entity.getParkingStatus();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    return ParkingLotResponseDto.builder()
        .id(entity.getId())
        .parkingCode(entity.getParkingCode())
        .parkingName(entity.getParkingName())
        .address(entity.getAddress())
        .district(entity.getDistrict())
        .parkingTypeName(entity.getParkingTypeName())
        .operationType(entity.getOperationType())
        .phone(entity.getPhone())
        .totalCapacity(entity.getTotalCapacity())
        .currentCount(status != null ? status.getCurrentCount() : 0)
        .availableCount(status != null ? status.getAvailableCount() : 0)
        .updatedAt(status != null && status.getUpdatedAt() != null ? status.getUpdatedAt().format(formatter) : null)
        .feeType(entity.getFeeType())
        .basicFee(entity.getBasicFee())
        .basicTime(entity.getBasicTime())
        .additionalFee(entity.getAdditionalFee())
        .additionalTime(entity.getAdditionalTime())
        .monthlyFee(entity.getMonthlyFee())
        .dayMaxFee(entity.getDayMaxFee())
        .lat(entity.getLat())
        .lng(entity.getLng())
        .weekdayStart(entity.getWeekdayStart())
        .weekdayEnd(entity.getWeekdayEnd())
        .weekendStart(entity.getWeekendStart())
        .weekendEnd(entity.getWeekendEnd())
        .holidayStart(entity.getHolidayStart())
        .holidayEnd(entity.getHolidayEnd())
        .build();
  }
}
