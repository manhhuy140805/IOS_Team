package io.volunteerapp.volunteer_app.Util;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.volunteerapp.volunteer_app.model.User;

/**
 * Service xử lý JWT Token
 * - Tạo token với claim "scope" chứa quyền dạng "ROLE_ADMIN" hoặc "ROLE_USER"
 * - Spring Security sẽ tự động parse scope thành authorities
 */
@Service
public class SecurityUtil {

    @Value("${jwt.base64-secret}")
    private String jwtKey;

    @Value("${jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public String generateAccessToken(User user) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

            JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                    .subject(user.getEmail())
                    .issuer("VolunteerApp")
                    .issueTime(new java.util.Date())
                    .expirationTime(new java.util.Date(
                            Instant.now().plus(this.accessTokenExpiration, ChronoUnit.SECONDS).toEpochMilli())) // Thời
                                                                                                                // gian
                                                                                                                // hết
                                                                                                                // hạn
                    .claim("userId", user.getId()) // Custom claim: user ID

                    // QUAN TRỌNG: claim "scope" chứa role
                    // VD: user.getRole() = "ADMIN" -> scope = "ROLE_ADMIN"
                    // Spring Security tự động parse "scope" thành
                    // SimpleGrantedAuthority("ROLE_ADMIN")
                    .claim("scope", "ROLE_" + user.getRole()) // Thêm prefix ROLE_
                    .build();

            // Tạo JWS Object và ký
            Payload payload = new Payload(jwtClaimsSet.toJSONObject());
            JWSObject jwsObject = new JWSObject(header, payload);
            jwsObject.sign(new MACSigner(jwtKey.getBytes()));

            // Trả về token string
            return jwsObject.serialize();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo access token", e);
        }
    }

    /**
     * Tạo JWT Refresh Token (thời gian sống lâu hơn)
     * 
     * @param user - User entity
     * @return Refresh token string
     */
    public String generateRefreshToken(User user) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

            JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                    .subject(user.getEmail())
                    .issuer("VolunteerApp")
                    .issueTime(new java.util.Date())
                    .expirationTime(new java.util.Date(
                            Instant.now().plus(this.refreshTokenExpiration, ChronoUnit.SECONDS).toEpochMilli()))
                    .claim("userId", user.getId())
                    .claim("type", "refresh") // Đánh dấu là refresh token
                    .build();

            Payload payload = new Payload(jwtClaimsSet.toJSONObject());
            JWSObject jwsObject = new JWSObject(header, payload);
            jwsObject.sign(new MACSigner(jwtKey.getBytes()));

            return jwsObject.serialize();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo refresh token", e);
        }
    }
}
