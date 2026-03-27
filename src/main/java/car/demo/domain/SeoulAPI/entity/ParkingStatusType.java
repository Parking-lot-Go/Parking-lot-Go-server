package car.demo.domain.SeoulAPI.entity;

import java.util.Arrays;

public enum ParkingStatusType {

  NON_REALTIME("2"), // 비 실시간
  REALTIME("1"),    // 실시간 연계
  NOT_LINKED("0");  // 미연계


  private final String code;

  ParkingStatusType(String code) {
    this.code = code;
  }

  public static ParkingStatusType from(String code) {
    if (code == null) return NOT_LINKED;
    return Arrays.stream(values())
        .filter(type -> type.code.equals(code) || type.name().equalsIgnoreCase(code))
        .findFirst()
        .orElse(NOT_LINKED);
  }
}