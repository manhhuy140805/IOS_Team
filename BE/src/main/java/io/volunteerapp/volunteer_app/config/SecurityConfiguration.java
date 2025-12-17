package io.volunteerapp.volunteer_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.volunteerapp.volunteer_app.Util.RestResponse;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 1. Client gửi request kèm JWT token trong header: Authorization: Bearer
 * 2. JwtDecoder decode và verify token
 * 3. JwtAuthenticationConverter chuyển claim "scope" thành GrantedAuthority
 * 4. @PreAuthorize ở controller kiểm tra quyền
 */
@Configuration
@EnableMethodSecurity // Bật @PreAuthorize, @PostAuthorize, @Secured
public class SecurityConfiguration {

    @Value("${jwt.base64-secret}")
    private String jwtKey;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Tạo secret key từ chuỗi base64
        SecretKey secretKey = new SecretKeySpec(
                jwtKey.getBytes(),
                "HmacSHA512");

        // Tạo NimbusJwtDecoder với secret key và thuật toán HS512
        return NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        // Không thêm prefix (mặc định là "SCOPE_")
        // Vì token của ta đã có "ROLE_ADMIN", không phải "ADMIN"
        grantedAuthoritiesConverter.setAuthorityPrefix("");

        // Lấy authorities từ claim "scope" (mặc định)
        // Nếu muốn đổi claim name, dùng:
        // grantedAuthoritiesConverter.setAuthoritiesClaimName("roles")

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            // Trả về JSON response với status 401
            response.setStatus(401);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            RestResponse<Object> restResponse = new RestResponse<>();
            restResponse.setStatus(401);
            restResponse.setError("Unauthorized");
            restResponse.setMessage("Token không hợp lệ hoặc đã hết hạn. Vui lòng đăng nhập lại.");

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(restResponse);
            response.getWriter().write(jsonResponse);
        };
    }

    /**
     * Custom AccessDeniedHandler: Xử lý lỗi 403 Forbidden
     * Khi user đã đăng nhập nhưng không đủ quyền truy cập
     */
    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            // Trả về JSON response với status 403
            response.setStatus(403);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            RestResponse<Object> restResponse = new RestResponse<>();
            restResponse.setStatus(403);
            restResponse.setError("Forbidden");
            restResponse.setMessage("Bạn không có quyền truy cập tài nguyên này.");

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(restResponse);
            response.getWriter().write(jsonResponse);
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http

                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(authz -> authz
                        // Cho phép tất cả endpoint auth (login, register)
                        // .requestMatchers("/api/v1/auth/**").permitAll()

                        // phân quyền bằng @PreAuthorize ở controller hoặc xài end pót config bỏ qua ở
                        // tần security cx đc
                        .anyRequest().permitAll())

                // Xử lý exception
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint()) // 401:
                        .accessDeniedHandler(customAccessDeniedHandler()) // 403
                )

                // Cấu hình OAuth2 Resource Server (JWT)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .decoder(jwtDecoder()) // Decode token
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()) // Chuyển thành Authentication
                        ));

        return http.build();
    }
}
