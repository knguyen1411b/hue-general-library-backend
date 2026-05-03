package org.app.backend.config;

import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.exception.CustomAccessDeniedHandler;
import org.app.backend.common.exception.CustomAuthenticationEntryPoint;
import org.app.backend.modules.auth.filter.JwtAuthenticationFilter;
import org.app.backend.modules.auth.security.JwtService;
import org.app.backend.modules.user.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppSecurityConfig {

  JwtService jwtService;
  CustomAccessDeniedHandler customAccessDeniedHandler;
  CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

  static String[] SWAGGER_WHITELIST = {"/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"};
  static String[] PUBLIC_ENDPOINTS = {"/"};
  static String[] AUTH_ENDPOINTS = {
    "/api/v1/auth/sign-in",
    "/api/v1/auth/sign-up",
    "/api/v1/auth/refresh",
    "/api/v1/auth/forgot-password",
    "/api/v1/auth/reset-password"
  };

  @Bean
  SecurityFilterChain securityFilterChain(
      HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable);
    http.cors(cors -> cors.configurationSource(corsConfigurationSource())); // ← SỬA Ở ĐÂY

    http.authorizeHttpRequests(
        auth ->
            auth.requestMatchers(HttpMethod.GET, SWAGGER_WHITELIST)
                .permitAll()
                .requestMatchers(HttpMethod.POST, AUTH_ENDPOINTS)
                .permitAll()
                .requestMatchers(HttpMethod.GET, PUBLIC_ENDPOINTS)
                .permitAll()
                .anyRequest()
                .authenticated());

    http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    http.exceptionHandling(
        ex ->
            ex.authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler));

    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  JwtAuthenticationFilter jwtAuthenticationFilter(UserService userService) {
    return new JwtAuthenticationFilter(jwtService, userService);
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() { // ← BEAN MỚI THAY THẾ
    CorsConfiguration cors = new CorsConfiguration();
    cors.setAllowCredentials(true);
    cors.setAllowedOriginPatterns(
        List.of(
            "http://localhost:*",
            "http://127.0.0.1:*",
            "https://localhost:*",
            "https://127.0.0.1:*",
            "https://*.app.github.dev"));
    cors.setAllowedMethods(List.of("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
    cors.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
    cors.setExposedHeaders(List.of("Authorization"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cors);
    return source;
  }
}
