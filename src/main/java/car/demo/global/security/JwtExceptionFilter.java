package car.demo.global.security;

import car.demo.global.exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {
    private final SecurityResponseHandler securityResponseHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
            securityResponseHandler.handleSecurityError(
                    request,
                    response,
                    ErrorCode.JWT_TOKEN_EXPIRED,
                    HttpStatus.UNAUTHORIZED,
                    e
            );
        } catch (car.demo.global.exception.custom.JwtException e) {
            log.warn("JWT exception: {}", e.getMessage());
            securityResponseHandler.handleSecurityError(
                    request,
                    response,
                    e.getErrorCode(),
                    HttpStatus.UNAUTHORIZED,
                    e
            );
        } catch (JwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            securityResponseHandler.handleSecurityError(
                    request,
                    response,
                    ErrorCode.JWT_TOKEN_INVALID,
                    HttpStatus.UNAUTHORIZED,
                    e
            );
        } catch (EntityNotFoundException e) {
            securityResponseHandler.handleSecurityError(
                    request,
                    response,
                    ErrorCode.NOT_FOUND_ENTITY,
                    HttpStatus.NOT_FOUND,
                    e
            );
        } catch (Exception e) {
            log.error("Unexpected error in filter: {}", e.getMessage(), e);
            securityResponseHandler.handleSecurityError(
                    request,
                    response,
                    ErrorCode.SERVER_ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e
            );
        }
    }
}
