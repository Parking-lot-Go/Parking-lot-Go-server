package car.demo.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class Oauth2RequestDTO {
    public record LoginAuthCode(
            @NotBlank String accessToken
    ) {}

    public record ReissueRequest(
            @NotBlank String refreshToken
    ) {}
}
