package car.demo.domain.user.controller;

import car.demo.domain.auth.service.JwtPrincipal;
import car.demo.domain.user.dto.UserDto;
import car.demo.domain.user.entity.NaviType;
import car.demo.domain.user.service.UserService;
import car.demo.global.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "사용자 관리 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 프로필 정보를 조회합니다.")
    @GetMapping("/me")
    public CommonResponse<UserDto> getMyProfile(@AuthenticationPrincipal JwtPrincipal principal) {
        UserDto userDto = userService.getById(principal.getId());
        return CommonResponse.ok(userDto);
    }

    @Operation(summary = "내비게이션 설정 변경", description = "사용자의 기본 내비게이션 앱 설정을 변경합니다. (KAKAO, NAVER)")
    @PatchMapping("/navi")
    public CommonResponse<UserDto> updateNaviType(
            @AuthenticationPrincipal JwtPrincipal principal,
            @RequestParam NaviType naviType) {
        UserDto userDto = userService.updateNaviType(principal.getId(), naviType);
        return CommonResponse.ok(userDto);
    }
}
