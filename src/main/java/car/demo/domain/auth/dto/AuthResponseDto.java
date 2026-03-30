package car.demo.domain.auth.dto;

import car.demo.domain.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginResponse {
        private UserDto user;
        private JwtTokenDto token;
    }
}
