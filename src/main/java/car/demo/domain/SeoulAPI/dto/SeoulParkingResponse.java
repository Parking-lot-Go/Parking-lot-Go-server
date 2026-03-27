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
public class SeoulParkingResponse {

    @JsonProperty("GetParkingInfo")
    private ParkingInfoContainer getParkingInfo;

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class ParkingInfoContainer {
        @JsonProperty("list_total_count")
        private Integer listTotalCount;

        @JsonProperty("RESULT")
        private ParkingResult result;

        @JsonProperty("row")
        private List<ParkingRow> row;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class ParkingResult {
        @JsonProperty("CODE")
        private String code;

        @JsonProperty("MESSAGE")
        private String message;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class ParkingRow implements ParkingLotData {
        @JsonProperty("PKLT_CD")
        private String parkingCode;

        @JsonProperty("PKLT_NM")
        private String parkingName;

        @JsonProperty("ADDR")
        private String address;

        @JsonProperty("PKLT_TYPE")
        private String parkingType;

        @JsonProperty("PRK_TYPE_NM")
        private String parkingTypeName;

        @JsonProperty("OPER_SE")
        private String operationSelection;

        @JsonProperty("OPER_SE_NM")
        private String operationSelectionName;

        @JsonProperty("TELNO")
        private String telNo;

        @JsonProperty("PRK_STTS_YN")
        private String parkingStatusYn;

        @JsonProperty("PRK_STTS_NM")
        private String parkingStatusName;

        @JsonProperty("TPKCT")
        private Double totalParkingCapacity;

        @JsonProperty("NOW_PRK_VHCL_CNT")
        private Double currentParkingCount;

        @JsonProperty("NOW_PRK_VHCL_UPDT_TM")
        private String currentParkingUpdateTime;

        @JsonProperty("PAY_YN")
        private String payYn;

        @JsonProperty("PAY_YN_NM")
        private String payYnName;

        @JsonProperty("NGHT_PAY_YN")
        private String nightPayYn;

        @JsonProperty("NGHT_PAY_YN_NM")
        private String nightPayYnName;

        @JsonProperty("WD_OPER_BGNG_TM")
        private String weekdayBeginTime;

        @JsonProperty("WD_OPER_END_TM")
        private String weekdayEndTime;

        @JsonProperty("WE_OPER_BGNG_TM")
        private String weekendBeginTime;

        @JsonProperty("WE_OPER_END_TM")
        private String weekendEndTime;

        @JsonProperty("LHLDY_OPER_BGNG_TM")
        private String holidayBeginTime;

        @JsonProperty("LHLDY_OPER_END_TM")
        private String holidayEndTime;

        @JsonProperty("SAT_CHGD_FREE_SE")
        private String saturdayPayYn;

        @JsonProperty("SAT_CHGD_FREE_NM")
        private String saturdayPayYnName;

        @JsonProperty("LHLDY_CHGD_FREE_SE")
        private String holidayPayYn;

        @JsonProperty("LHLDY_CHGD_FREE_SE_NAME")
        private String holidayPayYnName;

        @JsonProperty("PRD_AMT")
        private String periodicTicketAmount;

        @JsonProperty("STRT_PKLT_MNG_NO")
        private String managementNo;

        @JsonProperty("BSC_PRK_CRG")
        private Double basicParkingCharge;

        @JsonProperty("BSC_PRK_HR")
        private Double basicParkingTime;

        @JsonProperty("ADD_PRK_CRG")
        private Double addParkingCharge;

        @JsonProperty("ADD_PRK_HR")
        private Double addParkingTime;

        @JsonProperty("BUS_BSC_PRK_CRG")
        private Double busBasicParkingCharge;

        @JsonProperty("BUS_BSC_PRK_HR")
        private Double busBasicParkingTime;

        @JsonProperty("BUS_ADD_PRK_HR")
        private Double busAddParkingTime;

        @JsonProperty("BUS_ADD_PRK_CRG")
        private Double busAddParkingCharge;

        @JsonProperty("DAY_MAX_CRG")
        private Double dayMaxCharge;

        @JsonProperty("SHRN_PKLT_MNG_NM")
        private String sharingParkingManagementName;

        @JsonProperty("SHRN_PKLT_MNG_URL")
        private String sharingParkingManagementUrl;

        @JsonProperty("SHRN_PKLT_YN")
        private String sharingParkingYn;

        @JsonProperty("SHRN_PKLT_ETC")
        private String sharingParkingEtc;

        @JsonProperty("LAT")
        private String lat;

        @JsonProperty("LOT")
        private String lot;
    }
}
