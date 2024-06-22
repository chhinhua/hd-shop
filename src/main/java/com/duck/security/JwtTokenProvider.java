package com.duck.security;

import com.duck.exception.APIException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    @Autowired
    private MessageSource messageSource;

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-milliseconds}")
    private Long jwtExpirationDate;

    /**
     * Generates a JWT token based on the provided authentication.
     *
     * @param authentication The authentication object containing user information.
     * @return The generated JWT token.
     */
    public String generateToken(Authentication authentication) {
        // Retrieve the username from the authentication object
        String username = authentication.getName();

        // Calculate the current date and the date when the token will expire
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        // Build the JWT token with subject (username), issue date, expiration date, and signature
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();

        // Return the generated token
        return token;
    }

    private Key key() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecret)
        );
    }

    /**
     * Extracts the username from a JWT token.
     *
     * @param token The JWT token from which to retrieve the username.
     * @return The username associated with the token.
     */
    public String getUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();

        String username = claims.getSubject();
        return username;
    }

    /**
     * Validates the provided JWT token.
     *
     * @param token The JWT token to be validated.
     * @return True if the token is valid; false otherwise.
     * @throws APIException If the token is invalid or has expired, an exception is thrown with an appropriate message and HTTP status.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parse(token);
            return true;

        } catch (MalformedJwtException | SignatureException exception) {
            throw new APIException(getMessage("invalid-jwt-token"));
        } catch (ExpiredJwtException exception) {
            throw new APIException(getMessage("expired-jwt-token"));
        } catch (UnsupportedJwtException exception) {
            throw new APIException(getMessage("unsupported-jwt-token"));
        } catch (IllegalArgumentException exception) {
            throw new APIException(getMessage("jwt-claims-string-is-empty"));
        }

        // TODO chưa handle được jwt ra message
    }

    public boolean checkExpiredToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parse(token);
            return true;
        } catch (ExpiredJwtException exception) {
           return false;
        }
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
