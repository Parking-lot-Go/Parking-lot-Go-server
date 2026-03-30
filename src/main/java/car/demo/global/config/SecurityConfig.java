package car.demo.global.config;

import car.demo.global.security.JwtAuthenticationFilter;
import car.demo.global.security.JwtExceptionFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final CorsConfigurationSource corsConfigurationSource;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final JwtExceptionFilter jwtExceptionFilter;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .cors(cors -> cors.configurationSource(corsConfigurationSource))
        .formLogin(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);

    return http.build();

  }
}
