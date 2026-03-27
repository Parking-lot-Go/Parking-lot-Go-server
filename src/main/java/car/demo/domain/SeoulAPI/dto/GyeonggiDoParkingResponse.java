package car.demo.domain.SeoulAPI.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class GyeonggiDoParkingResponse {

    @JsonProperty("ParkingPlace")
    private List<ParkingPlaceContainer> parkingPlace;

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class ParkingPlaceContainer {
        @JsonProperty("head")
        private List<Object> head;

        @JsonProperty("row")
        private List<ParkingRow> row;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class ParkingRow implements ParkingLotData {
        @JsonProperty("DATA_REGIST_INST_CD")
        private String dataRegistInstCd;

        @JsonProperty("DATA_REGIST_INST_NM")
        private String dataRegistInstNm;

        @JsonProperty("SIGUN_NM")
        private String sigunNm;

        @JsonProperty("PARKPLC_MANAGE_NO")
        private String parkingManageNo;

        @JsonProperty("PARKPLC_NM")
        private String parkingName;

        @JsonProperty("PARKPLC_DIV_NM")
        private String parkingDivNm;

        @JsonProperty("PARKPLC_TYPE")
        private String parkingType;

        @JsonProperty("LOCPLC_ROADNM_ADDR")
        private String roadNameAddress;

        @JsonProperty("LOCPLC_LOTNO_ADDR")
        private String lotNoAddress;

        @JsonProperty("PARKNG_COMPRT_PLANE_CNT")
        private Double parkingPlaneCount;

        @JsonProperty("LANDGRAD_DIV_NM")
        private String landgradDivNm;

        @JsonProperty("SUBTL_IMPLMTN_DIV_NM")
        private String subtlImplmtnDivNm;

        @JsonProperty("TMP02")
        private String operatingDays;

        @JsonProperty("WKDAY_OPERT_BEGIN_TM")
        private String weekdayBeginTime;

        @JsonProperty("WKDAY_OPERT_END_TM")
        private String weekdayEndTime;

        @JsonProperty("SAT_OPERT_BEGIN_TM")
        private String weekendBeginTime;

        @JsonProperty("SAT_OPERT_END_TM")
        private String weekendEndTime;

        @JsonProperty("HOLIDAY_OPERT_BEGIN_TM")
        private String holidayBeginTime;

        @JsonProperty("HOLIDAY_OPERT_END_TM")
        private String holidayEndTime;

        @JsonProperty("CHRG_INFO")
        private String chargeInfo;

        @JsonProperty("PARKNG_BASIS_TM")
        private Double basicParkingTime;

        @JsonProperty("PARKNG_BASIS_USE_CHRG")
        private Double basicParkingCharge;

        @JsonProperty("ADD_UNIT_TM")
        private Double addParkingTime;

        @JsonProperty("ADD_UNIT_TM2_WITHIN_USE_CHRG")
        private Double addParkingCharge;

        @JsonProperty("DAY1_PARKTK_CHRG_APPLCTN_TM")
        private Double day1ParkingApplicationTime;

        @JsonProperty("DAY1_PARKTK_USE_CHRG")
        private Double day1ParkingCharge;

        @JsonProperty("MT_CMMTICKT_WEEK_USE_CHRG")
        private String monthlyTicketAmount;

        @JsonProperty("SETTLE_METH")
        private String settlementMethod;

        @JsonProperty("SPCLABLT_MATR")
        private String specialNote;

        @JsonProperty("MANAGE_INST_NM")
        private String managementInstNm;

        @JsonProperty("CONTCT_NO")
        private String contactNo;

        @JsonProperty("REFINE_WGS84_LAT")
        private String lat;

        @JsonProperty("REFINE_WGS84_LOGT")
        private String lot;

        @JsonProperty("TMP01")
        private String handicappedOnly;

        @JsonProperty("DATA_STD_DE")
        private String dataDate;

        @JsonProperty("REFINE_ZIP_CD")
        private String zipCode;

        @JsonProperty("SIGUN_CD")
        private String sigunCd;

        // Implement ParkingLotData methods
        @Override
        public String getParkingCode() {
            return parkingManageNo;
        }

        @Override
        public String getParkingName() {
            return parkingName;
        }

        @Override
        public String getAddress() {
            return roadNameAddress != null && !roadNameAddress.isBlank() ? roadNameAddress : lotNoAddress;
        }

        @Override
        public String getParkingType() {
            return parkingType;
        }

        @Override
        public String getParkingTypeName() {
            return parkingDivNm;
        }

        @Override
        public String getOperationSelectionName() {
            return operatingDays;
        }

        @Override
        public String getTelNo() {
            return contactNo;
        }

        @Override
        public Double getTotalParkingCapacity() {
            return parkingPlaneCount;
        }

        @Override
        public String getParkingStatusYn() {
            return "0"; // Gyeonggi-do this API doesn't seem to have real-time status
        }

        @Override
        public Double getCurrentParkingCount() {
            return 0.0;
        }

        @Override
        public String getCurrentParkingUpdateTime() {
            return dataDate;
        }

        @Override
        public String getWeekdayBeginTime() {
            return weekdayBeginTime;
        }

        @Override
        public String getWeekdayEndTime() {
            return weekdayEndTime;
        }

        @Override
        public String getWeekendBeginTime() {
            return weekendBeginTime;
        }

        @Override
        public String getWeekendEndTime() {
            return weekendEndTime;
        }

        @Override
        public String getHolidayBeginTime() {
            return holidayBeginTime;
        }

        @Override
        public String getHolidayEndTime() {
            return holidayEndTime;
        }

        @Override
        public String getPayYnName() {
            return chargeInfo;
        }

        @Override
        public Double getBasicParkingCharge() {
            return basicParkingCharge;
        }

        @Override
        public Double getBasicParkingTime() {
            return basicParkingTime;
        }

        @Override
        public Double getAddParkingCharge() {
            return addParkingCharge;
        }

        @Override
        public Double getAddParkingTime() {
            return addParkingTime;
        }

        @Override
        public String getPeriodicTicketAmount() {
            return monthlyTicketAmount;
        }

        @Override
        public Double getDayMaxCharge() {
            return day1ParkingCharge;
        }

        @Override
        public String getLat() {
            return lat;
        }

        @Override
        public String getLot() {
            return lot;
        }
    }
}
