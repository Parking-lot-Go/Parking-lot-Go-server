package car.demo.domain.user.dto;

import car.demo.domain.auth.dto.JwtTokenDto;
import car.demo.domain.user.entity.LoginType;
import car.demo.domain.user.entity.NaviType;
import car.demo.domain.user.entity.Role;
import car.demo.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private LoginType loginType;
    private Role role;
    private Boolean isRegister;
    private NaviType naviType;

    @Getter(onMethod_ = {@JsonIgnore, @Schema(hidden = true)})
    private boolean isLogin;

    @Getter(onMethod_ = {@JsonIgnore, @Schema(hidden = true)})
    private boolean isActive;

    @Getter(onMethod_ = {@JsonIgnore, @Schema(hidden = true)})
    private String socialId;

    @Getter(onMethod_ = {@JsonIgnore, @Schema(hidden = true)})
    private String accessToken;

    @Getter(onMethod_ = {@JsonIgnore, @Schema(hidden = true)})
    private String refreshToken;

    public static UserDto authUserDTO(User user, Boolean isRegister) {
        return UserDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .loginType(user.getLoginType())
                .profileImageUrl(user.getProfileImageUrl())
                .role(user.getRole())
                .naviType(user.getNaviType())
                .isRegister(isRegister)
                .isActive(user.isActive()).build();
    }

    public static UserDto reissue(Claims claims) {
        return UserDto.builder()
                .userId(Long.parseLong(claims.getSubject()))
                .nickname(claims.get("nickname").toString())
                .role(Role.valueOf(claims.get("role").toString()))
                .build();
    }

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .loginType(user.getLoginType())
                .socialId(user.getSocialId())
                .role(user.getRole())
                .naviType(user.getNaviType())
                .isActive(user.isActive()).build();
    }

    public void addTokens(JwtTokenDto jwtTokenDto) {
        this.accessToken = jwtTokenDto.getAccessToken();
        this.refreshToken = jwtTokenDto.getRefreshToken();
    }

    public JwtTokenDto toJwtTokenDto() {
        return JwtTokenDto.of(this.accessToken, this.refreshToken);
    }

    public String userIdStr() {
        return String.valueOf(this.userId);
    }

    public boolean isKakao() {
        return this.loginType == LoginType.KAKAO;
    }
}
