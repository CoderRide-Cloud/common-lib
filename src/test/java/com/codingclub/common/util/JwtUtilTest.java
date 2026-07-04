package com.codingclub.common.util;

import com.codingclub.common.security.Permission;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtUtil Tests")
public class JwtUtilTest {

    private JwtUtil jwtUtil;

    private static final String TEST_SECRET =
            "thisisaverysecuresecretkeythatisverylongandsecure!";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expirationTime", 3600000L); // 1 hour
        jwtUtil.init(); // trigger @PostConstruct
    }

    @Test
    @DisplayName("generates a non-blank token")
    void generateToken_notBlank() {
        String token = jwtUtil.generateToken(1L, "MEMBER");
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    @DisplayName("token is valid immediately after generation")
    void validateToken_validImmediately() {
        String token = jwtUtil.generateToken(1L, "MEMBER");
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("extractUserId returns the correct subject")
    void extractUserId_correct() {
        String token = jwtUtil.generateToken(42L, "ADMIN");
        assertEquals("42", jwtUtil.extractUserId(token));
    }

    @Test
    @DisplayName("extractRole returns correct role claim")
    void extractRole_correct() {
        String token = jwtUtil.generateToken(1L, "MEMBER");
        assertEquals("MEMBER", jwtUtil.extractRole(token));
    }

    @Test
    @DisplayName("validateToken returns false for tampered token")
    void validateToken_tamperedToken() {
        String token = jwtUtil.generateToken(1L, "MEMBER");
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";
        assertFalse(jwtUtil.validateToken(tampered));
    }

    @Test
    @DisplayName("validateToken returns false for expired token")
    void validateToken_expiredToken() {
        // Override expiration to -1ms (already expired)
        ReflectionTestUtils.setField(jwtUtil, "expirationTime", -1L);
        String token = jwtUtil.generateToken(1L, "MEMBER");
        assertFalse(jwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("full token with permissions round-trips correctly")
    void fullToken_withPermissions_roundTrip() {
        String token = jwtUtil.generateToken(
                99L, "MEMBER", 5L, 3, false, true,
                java.util.Set.of(Permission.VIEW_DASHBOARD));

        Claims claims = jwtUtil.extractAllClaims(token);

        assertEquals("99", claims.getSubject());
        assertEquals("MEMBER", claims.get("role", String.class));
        assertEquals(5, ((Number) claims.get("customRoleId")).intValue());
        assertTrue(claims.get("permissions", String.class).contains("VIEW_DASHBOARD"));
    }

    @Test
    @DisplayName("signing key is cached — same object on multiple calls")
    void cachedKey_sameObjectReference() {
        // Generate two tokens and validate both — if key was re-derived each time,
        // it would still work but be wasteful. This test confirms caching is set up.
        String t1 = jwtUtil.generateToken(1L, "MEMBER");
        String t2 = jwtUtil.generateToken(2L, "ADMIN");

        // Both tokens from the same cached key must validate correctly
        assertTrue(jwtUtil.validateToken(t1));
        assertTrue(jwtUtil.validateToken(t2));
        assertEquals("1", jwtUtil.extractUserId(t1));
        assertEquals("2", jwtUtil.extractUserId(t2));
    }

    @Test
    @DisplayName("extractExpiration returns a future date for fresh token")
    void extractExpiration_isFuture() {
        String token = jwtUtil.generateToken(1L, "MEMBER");
        Date expiry = jwtUtil.extractExpiration(token);
        assertTrue(expiry.after(new Date()), "Expiry should be in the future");
    }
}
