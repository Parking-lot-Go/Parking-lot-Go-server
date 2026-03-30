package car.demo.domain.auth.controller;

import car.demo.domain.auth.dto.AuthResponseDto;
import car.demo.domain.auth.dto.Oauth2RequestDTO;
import car.demo.domain.auth.service.oauth2.Oauth2ServiceFactory;
import car.demo.domain.auth.service.JwtPrincipal;
import car.demo.domain.oauth.entity.PrincipalDetails;
import car.demo.domain.user.dto.UserDto;
import car.demo.global.dto.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final Oauth2ServiceFactory oauth2ServiceFactory;
    private final car.demo.domain.auth.service.JwtService jwtService;

    @PostMapping("/login/{provider}")
    public CommonResponse<AuthResponseDto.LoginResponse> login(@PathVariable String provider,
                                                               @RequestBody @Valid Oauth2RequestDTO.LoginAuthCode payload) {
        oauth2ServiceFactory.validateProvider(provider, payload.accessToken());
        UserDto userDto = oauth2ServiceFactory.getService(provider).login(payload.accessToken());

        userDto.addTokens(jwtService.generateTokens(userDto));

        PrincipalDetails principal = new PrincipalDetails(userDto);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        return CommonResponse.ok(new AuthResponseDto.LoginResponse(userDto, userDto.toJwtTokenDto()));
    }

    @PostMapping("/logout")
    public CommonResponse<Void> logout(@AuthenticationPrincipal PrincipalDetails principal) {
        if (principal != null && principal.getUserDto() != null) {
            jwtService.logout(principal.getUserDto().getUserId());
        }
        return CommonResponse.ok(null);
    }
}
