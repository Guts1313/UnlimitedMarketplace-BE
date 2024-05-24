package unlimitedmarketplace.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccessTokenEncoderDecoderImpl implements AccessTokenEncoder, AccessTokenDecoder {
    private final Key key;
    private static final String ROLES  = "roles";
    private static final Logger log = LoggerFactory.getLogger(AccessTokenEncoderDecoderImpl.class);

    public AccessTokenEncoderDecoderImpl(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }
    public String encodeAndGetId(String username, Long userId, Collection<? extends GrantedAuthority> authorities) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(ROLES, List.copyOf(authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList()));
        claims.put("id", userId); // Add the user ID to the token
        Instant now = Instant.now();
        try {
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(now.plus(10, ChronoUnit.MINUTES))) // Adjusted for testing purposes
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
            log.info("Successfully generated JWT: {}", token);
            return token;
        } catch (JwtException e) {
            log.error("Failed to generate JWT", e);
            return null;
        }
    }

    public String encode(String username, Collection<? extends GrantedAuthority> authorities) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(ROLES, List.copyOf(authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList()));


        Instant now = Instant.now();
        try {
            log.info("Attempting to generate JWT for user: {}", username);
            log.info("JWT claims set: {}", claims);

            String token = Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(now.plus(10, ChronoUnit.MINUTES))) // Adjusted from seconds to minutes for better testing
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            log.info("Successfully generated JWT: {}", token);
            return token;
        } catch (JwtException e) {
            log.error("Failed to generate JWT for user: {} with error: {}", username, e.getMessage());
            return null;
        }
    }



    public AccessToken decodeEncoded(String accessTokenEncoded) {
        try {
            Jwt<?, Claims> jwt = Jwts.parserBuilder().setSigningKey(key).setAllowedClockSkewSeconds(30000) // Allow 30 seconds clock skew
                    .build()
                    .parseClaimsJws(accessTokenEncoded);
            Claims claims = jwt.getBody();

            List<String> rolesList = claims.get(ROLES, List.class);
            Long userId = claims.get("id", Long.class);

            // Directly collect roles without adding the ROLE_ prefix
            Set<String> rolesSet = rolesList.stream()
                    .map(String::toUpperCase) // Convert to upper case for consistency
                    .collect(Collectors.toSet());

            log.info("Decoded roles: {}", rolesSet);
            return new AccessTokenImpl(claims.getSubject(), userId, rolesSet);
        } catch (JwtException e) {
            throw new InvalidAccessTokenException(e.getMessage());
        }
    }

    @Override
    public Claims decode(String accessTokenEncoded) throws InvalidAccessTokenException {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .setAllowedClockSkewSeconds(30000) // Allow 5 minutes of clock skew
                    .build()
                    .parseClaimsJws(accessTokenEncoded)
                    .getBody();
        } catch (JwtException e) {
            log.error("JWT decoding failed", e);
            throw new InvalidAccessTokenException("Failed to decode the access token.");
        }
    }
}
