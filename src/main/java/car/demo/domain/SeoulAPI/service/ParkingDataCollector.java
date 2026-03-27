package car.demo.domain.SeoulAPI.service;

import car.demo.global.constants.Province;

public interface ParkingDataCollector {
    void collect();

    /**
     * 스케줄러에 의해 자동으로 수집될 수 있는지 여부를 반환합니다.
     * 기본값은 false이며, 스케줄링이 필요한 구현체에서 true를 반환하도록 재정의합니다.
     */
    default boolean isAutoCollectible() {
        return false;
    }

    Province getProvince();


}
