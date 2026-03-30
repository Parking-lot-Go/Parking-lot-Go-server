package car.demo.domain.auth.service;

import car.demo.domain.auth.dto.JwtTokenDto;
import car.demo.domain.auth.entity.Token;
import car.demo.domain.auth.entity.TokenType;
import car.demo.domain.auth.repository.TokenRepository;
import car.demo.domain.user.dto.UserDto;
import car.demo.global.exception.custom.JwtException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static car.demo.global.exception.ErrorCode.JWT_TOKEN_EXPIRED;
import static car.demo.global.exception.ErrorCode.JWT_TOKEN_INVALID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class JwtService {

    private final TokenRepository tokenRepository;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.issuer:car-app}")
    private String issuer;

    @Value("${jwt.access-token-expiry:60}")
    private long accessTokenExpiryMinutes;

    @Value("${jwt.refresh-token-expiry:30}")
    private long refreshTokenExpiryDays;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Transactional
    public String getRefreshToken(UserDto userDto) {
        return tokenRepository.findByUserIdAndType(userDto.getUserId(), TokenType.REFRESH_TOKEN)
                .map(existing -> {
                    if (isTokenExpired(existing.getToken())) {
                        Result result = buildRefreshJwt(userDto);
                        existing.setExpiredAt(result.expiredAt());
                        existing.setToken(result.jwtToken());
                        return result.jwtToken();
                    }
                    return existing.getToken();
                })
                .orElseGet(() -> generateRefreshToken(userDto).getToken());
    }

    public JwtTokenDto generateTokens(UserDto userDto) {
        return JwtTokenDto.of(generateAccessToken(userDto), generateRefreshToken(userDto).getToken());
    }

    public JwtTokenDto generateTokens(JwtPrincipal principal) {
        String access = generateAccessToken(principal);
        if (principal instanceof UserJwtPrincipalAdapter u) {
            String refresh = generateRefreshToken(u.getUserDto()).getToken();
            return JwtTokenDto.of(access, refresh);
        }
        return JwtTokenDto.of(access, null);
    }

    @Transactional
    public void softDelete(Long userId) {
        tokenRepository.findAllByUserId(userId)
                .forEach(Token::markDeleted);
    }

    @Transactional
    public void logout(Long userId) {
        tokenRepository.findAllByUserId(userId)
                .forEach(tokenRepository::delete);
    }

    @Transactional
    public String reissueTokensFromRefresh(String refreshToken) {
        Claims claims = validateToken(refreshToken);
        if (!"refresh".equals(claims.get("type"))) {
            throw new JwtException(JWT_TOKEN_EXPIRED);
        }
        boolean tokenExpired = isTokenExpired(refreshToken);
        if (!tokenExpired) {
            return generateAccessToken(UserDto.reissue(claims));
        }

        tokenRepository.deleteByToken(refreshToken);
        throw new JwtException(JWT_TOKEN_INVALID);
    }

    public String generateAccessToken(UserDto userDto) {
        return generateAccessToken(new UserJwtPrincipalAdapter(userDto));
    }

    public String generateAccessToken(JwtPrincipal principal) {
        Instant now = Instant.now();
        Instant expiry = now.plus(accessTokenExpiryMinutes, ChronoUnit.MINUTES);

        JwtBuilder builder = Jwts.builder()
                .issuer(issuer)
                .subject(String.valueOf(principal.getId()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .claim("type", "access");

        if (principal.getRoleAsString() != null) {
            builder.claim("role", principal.getRoleAsString());
        }

        if (principal.getNickname() != null) {
            builder.claim("nickname", principal.getNickname());
        }
        if (principal.getPlatform() != null) {
            builder.claim("platform", principal.getPlatform());
        }

        return builder.signWith(getSigningKey())
                .compact();
    }

    public Token generateRefreshToken(UserDto userDto) {
        Result result = buildRefreshJwt(userDto);
        return tokenRepository.save(Token.of(result.jwtToken(), result.expiredAt(), userDto));
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationFromToken(token);
            return expiration.before(new Date());
        } catch (car.demo.global.exception.custom.JwtException | io.jsonwebtoken.JwtException e) {
            return true;
        }
    }

    public Claims validateToken(String token) {
        Claims claims = parseToken(token);
        if ("refresh".equals(claims.get("type"))) {
            tokenRepository.findByToken(token).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 토큰입니다."));
        }
        return claims;
    }

    public Date getExpirationFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration();
    }

    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.error("JWT 토큰 만료: {}", e.getMessage());
            throw new car.demo.global.exception.custom.JwtException(JWT_TOKEN_EXPIRED);
        } catch (io.jsonwebtoken.JwtException e) {
            log.error("JWT 토큰 파싱 실패: {}", e.getMessage());
            throw new car.demo.global.exception.custom.JwtException(JWT_TOKEN_INVALID);
        }
    }

    private Result buildRefreshJwt(UserDto userDto) {
        Instant now = Instant.now();
        Instant expiry = now.plus(refreshTokenExpiryDays, ChronoUnit.DAYS);

        String jwtToken = Jwts.builder()
                .issuer(issuer)
                .subject(String.valueOf(userDto.getUserId()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .claim("type", "refresh")
                .claim("actor", "user")
                .claim("nickname", userDto.getNickname())
                .claim("role", userDto.getRole())
                .signWith(getSigningKey())
                .compact();

        LocalDateTime expiredAt = LocalDateTime.ofInstant(expiry, ZoneId.systemDefault());
        return new Result(jwtToken, expiredAt);
    }

    private record Result(String jwtToken, LocalDateTime expiredAt) {
    }
}
