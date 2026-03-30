package car.demo.domain.auth.service.oauth2;

import car.demo.domain.user.dto.UserDto;

public interface Oauth2Service {
    UserDto login(String accessToken);
    String getProviderName();
}
