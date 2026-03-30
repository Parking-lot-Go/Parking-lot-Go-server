package car.demo.domain.auth.service;

import car.demo.domain.user.dto.UserDto;
import car.demo.domain.user.entity.Role;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Test
    @DisplayName("JWT 빌더 로드 및 토큰 생성 테스트 - UnknownClassException 발생 여부 확인")
    void generateTokenTest() {
        // Given
        UserDto userDto = UserDto.builder()
                .userId(1L)
                .nickname("testUser")
                .role(Role.ROLE_USER)
                .build();

        // When & Then
        // 이 시점에서 io.jsonwebtoken.impl.DefaultJwtBuilder를 로드하려 시도합니다.
        assertDoesNotThrow(() -> {
            String token = jwtService.generateAccessToken(userDto);
            assertThat(token).isNotNull();
        });
    }
}
