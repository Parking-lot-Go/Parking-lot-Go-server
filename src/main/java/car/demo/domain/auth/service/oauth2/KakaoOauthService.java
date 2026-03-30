package car.demo.domain.auth.service.oauth2;

import car.demo.domain.auth.dto.KakaoUserResponse;
import car.demo.domain.user.dto.UserDto;
import car.demo.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoOauthService implements Oauth2Service {

    private final WebClient webClient = WebClient.builder().build();
    private final UserService userService;

    @Override
    public UserDto login(String accessToken) {
        KakaoUserResponse me = webClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .headers(h -> h.setBearerAuth(accessToken))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(KakaoUserResponse.class)
                .block();

        if (me == null) {
            throw new RuntimeException("Failed to fetch Kakao user info");
        }

        return userService.signUpOrLogin(me);
    }

    @Override
    public String getProviderName() {
        return "kakao";
    }
}
