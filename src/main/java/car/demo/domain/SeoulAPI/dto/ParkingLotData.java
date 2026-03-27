package car.demo.domain.SeoulAPI.dto;

public interface ParkingLotData {
    String getParkingCode();
    String getParkingName();
    String getAddress();
    String getParkingType();
    String getParkingTypeName();
    String getOperationSelectionName();
    String getTelNo();
    Double getTotalParkingCapacity();
    String getParkingStatusYn();
    Double getCurrentParkingCount();
    String getCurrentParkingUpdateTime();
    String getWeekdayBeginTime();
    String getWeekdayEndTime();
    String getWeekendBeginTime();
    String getWeekendEndTime();
    String getHolidayBeginTime();
    String getHolidayEndTime();
    String getPayYnName();
    Double getBasicParkingCharge();
    Double getBasicParkingTime();
    Double getAddParkingCharge();
    Double getAddParkingTime();
    String getPeriodicTicketAmount();
    Double getDayMaxCharge();
    String getLat();
    String getLot();
}
