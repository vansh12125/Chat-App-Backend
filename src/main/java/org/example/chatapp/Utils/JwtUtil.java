package org.example.chatapp.Utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Configuration;

import java.security.Key;
import java.util.Date;

@Configuration
public class JwtUtil {

    private static final String SECRET =
            System.getenv("JWT_SECRET");

    private static final long EXPIRATION =
            1000 * 60 * 60 * 24 * 7; // 7 days

    private static final Key KEY =
            Keys.hmacShaKeyFor(SECRET.getBytes());

    public static String generateToken(String username) {

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + EXPIRATION)
                )
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String validateAndGetUsername(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
