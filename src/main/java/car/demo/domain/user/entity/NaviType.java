package car.demo.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NaviType {
    KAKAO("카카오내비"),
    NAVER("네이버지도");

    private final String description;
}
