package car.demo.global.security;

import car.demo.domain.auth.service.JwtService;
import car.demo.domain.oauth.entity.PrincipalDetails;
import car.demo.domain.user.dto.UserDto;
import car.demo.domain.user.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static car.demo.global.constatns.Security.WHITELISTED_URLS;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private final JwtService jwtService;
    private final UserService userService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader(AUTHORIZATION);
        String path = request.getRequestURI();

        if (isExcludedPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (header == null || !header.startsWith(BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }
        String jwtToken = header.substring(BEARER.length());

        jwtService.validateToken(jwtToken);
        setAuthenticationContext(jwtToken);
        filterChain.doFilter(request, response);
    }

    private boolean isExcludedPath(String path) {
        return WHITELISTED_URLS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private void setAuthenticationContext(String token) {
        Claims claims = jwtService.parseToken(token);
        Long userId = Long.parseLong(claims.getSubject());
        String role = claims.get("role") != null ? claims.get("role").toString() : null;
        
        if (role != null) {
            UserDto userDto = userService.getById(userId);
            PrincipalDetails principalDetails = new PrincipalDetails(userDto);
            UsernamePasswordAuthenticationToken authenticationToken = 
                    new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
    }
}
