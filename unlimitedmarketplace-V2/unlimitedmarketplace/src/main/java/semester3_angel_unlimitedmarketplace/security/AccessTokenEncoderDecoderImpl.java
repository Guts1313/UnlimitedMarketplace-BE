package semester3_angel_unlimitedmarketplace.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
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
    private static final Logger log = LoggerFactory.getLogger(AccessTokenEncoderDecoderImpl.class);

    public AccessTokenEncoderDecoderImpl(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public AccessToken decode(String accessTokenEncoded) {
        try {
            Jwt<?, Claims> jwt = Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(accessTokenEncoded);
            Claims claims = jwt.getBody();

            List<String> rolesList = claims.get("roles", List.class);
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
    public AccessToken encode(String accessTokenEncoded) {
        return null;
    }

    @Override
    public String encode(AccessToken accessToken) {
        Map<String, Object> claimsMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(accessToken.getRoles())) {
            claimsMap.put("roles", accessToken.getRoles());
        }
        if (accessToken.getUserId() != null) {
            claimsMap.put("id", accessToken.getUserId());
        }

        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(accessToken.getSubject())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(30, ChronoUnit.MINUTES)))
                .addClaims(claimsMap)
                .signWith(key)
                .compact();
    }
}
