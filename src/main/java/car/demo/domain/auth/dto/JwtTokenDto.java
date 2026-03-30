package car.demo.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JwtTokenDto {
    private String accessToken;
    private String refreshToken;

    public static JwtTokenDto of(String accessToken, String refreshToken) {
        return new JwtTokenDto(accessToken, refreshToken);
    }
}
