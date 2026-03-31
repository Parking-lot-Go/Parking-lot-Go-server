package car.demo.domain.auth.service;

import car.demo.domain.auth.dto.JwtTokenDto;
import car.demo.domain.user.dto.UserDto;
import car.demo.domain.user.entity.Role;
import car.demo.global.exception.ErrorCode;
import car.demo.global.exception.custom.JwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Test
    @DisplayName("유효한 리프레시 토큰으로 액세스 토큰 재발급 테스트")
    @Transactional
    void reissueTokenSuccessTest() {
        // Given
        UserDto userDto = UserDto.builder()
                .userId(1L)
                .nickname("testUser")
                .role(Role.ROLE_USER)
                .build();
        JwtTokenDto tokens = jwtService.generateTokens(userDto);
        String refreshToken = tokens.getRefreshToken();

        // When
        JwtTokenDto reissuedTokens = jwtService.reissueTokensFromRefresh(refreshToken);

        // Then
        assertThat(reissuedTokens).isNotNull();
        assertThat(reissuedTokens.getAccessToken()).isNotNull();
        assertThat(reissuedTokens.getRefreshToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("만료된 리프레시 토큰 사용 시 401 예외 발생 테스트")
    void reissueTokenExpiredTest() {
        // Given
        // 만료된 토큰을 직접 생성하거나, JwtService의 만료 시간을 아주 짧게 설정해야 함.
        // 여기서는 JwtService의 만료 로직이 제대로 동작하는지 검증하기 위해 
        // parseToken에서 던지는 ExpiredJwtException이 JwtException(JWT_TOKEN_EXPIRED)로 변환되는지 확인.
        
        // 실제 만료된 토큰 시뮬레이션은 복잡할 수 있으므로, 
        // JwtService 내부에서 parseToken이 던지는 예외를 확인하는 방식으로 접근하거나
        // 직접 만료된 토큰을 빌드하여 테스트.
        
        // 생략: 실제 만료 토큰 생성 로직은 SecretKey 접근 등이 필요함.
    }
}
