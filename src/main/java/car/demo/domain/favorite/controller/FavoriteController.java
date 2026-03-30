package car.demo.domain.favorite.controller;

import car.demo.domain.SeoulAPI.dto.CursorResponseDto;
import car.demo.domain.SeoulAPI.dto.ParkingLotResponseDto;
import car.demo.domain.auth.service.JwtPrincipal;
import car.demo.domain.favorite.service.FavoriteService;
import car.demo.global.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Favorite", description = "주차장 즐겨찾기 API")
@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(summary = "즐겨찾기 토글", description = "주차장을 즐겨찾기에 추가하거나 해제합니다.")
    @PostMapping("/{parkingLotId}")
    public CommonResponse<Boolean> toggleFavorite(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long parkingLotId) {
        boolean isAdded = favoriteService.toggleFavorite(principal.getId(), parkingLotId);
        return CommonResponse.ok(isAdded); // true: 등록, false: 해제
    }

    @Operation(summary = "내 즐겨찾기 목록 조회", description = "로그인한 사용자의 즐겨찾기 주차장 목록을 커서 기반 페이징으로 조회합니다.")
    @GetMapping
    public CommonResponse<CursorResponseDto<ParkingLotResponseDto>> getMyFavorites(
            @AuthenticationPrincipal JwtPrincipal principal,
            @Parameter(description = "마지막으로 조회된 ID (첫 조회시 null)") @RequestParam(required = false) Long cursor,
            @Parameter(description = "조회 개수 (기본 10개)") @RequestParam(required = false) Integer size) {
        CursorResponseDto<ParkingLotResponseDto> favorites = favoriteService.getFavoriteParkingLots(principal.getId(), cursor, size);
        return CommonResponse.ok(favorites);
    }
}
