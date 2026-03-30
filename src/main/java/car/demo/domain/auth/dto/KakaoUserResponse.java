package car.demo.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserResponse {

    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    @NoArgsConstructor
    public static class KakaoAccount {
        private Profile profile;
        private String email;

        @Getter
        @NoArgsConstructor
        public static class Profile {
            private String nickname;
            @JsonProperty("profile_image_url")
            private String profileImageUrl;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class TokenResponse {
        @JsonProperty("access_token")
        private String accessToken;
        @JsonProperty("refresh_token")
        private String refreshToken;
        @JsonProperty("id_token")
        private String idToken;
    }
}
