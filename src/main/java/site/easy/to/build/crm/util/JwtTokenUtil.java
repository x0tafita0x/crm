package site.easy.to.build.crm.util;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenUtil {

    private static final String SECRET_KEY = "secret";
    private static final long EXPIRATION_TIME = 86400000L;

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    private boolean validateToken(String token) {
        try {
            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build();

            Claims claims = parser.parseClaimsJws(token).getBody();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build();

        Claims claims = parser.parseClaimsJws(token).getBody();

        return claims.getSubject();
    }
}

