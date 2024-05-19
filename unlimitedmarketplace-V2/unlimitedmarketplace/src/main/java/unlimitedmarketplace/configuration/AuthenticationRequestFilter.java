package unlimitedmarketplace.configuration;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import unlimitedmarketplace.security.AccessToken;
import unlimitedmarketplace.security.AccessTokenDecoder;
import unlimitedmarketplace.security.AccessTokenEncoderDecoderImpl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class AuthenticationRequestFilter extends OncePerRequestFilter {

    private static final String SPRING_SECURITY_ROLE_PREFIX = "";
    private static final Logger log = LoggerFactory.getLogger(AuthenticationRequestFilter.class);

    @Autowired
    private AccessTokenDecoder accessTokenDecoder;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // Skip filter for WebSocket upgrade requests
        if ("websocket".equalsIgnoreCase(request.getHeader("Upgrade"))) {
            chain.doFilter(request, response);
            return;
        }

        String requestTokenHeader = request.getHeader("Authorization");
        log.info("Received Authorization Header: {}", requestTokenHeader);
        if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String accessTokenString = requestTokenHeader.substring(7);
        log.info("Attempting to decode JWT: {}", accessTokenString);

        try {
            AccessToken accessToken = accessTokenDecoder.decodeEncoded(accessTokenString);
            setupSpringSecurityContext(accessToken);
            chain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("Error validating access token", e);
            sendAuthenticationError(response, "Invalid JWT: {}" + e.getMessage());
        }
    }


    private void sendAuthenticationError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
        response.flushBuffer();
    }

    private void setupSpringSecurityContext(AccessToken accessToken) {
        List<GrantedAuthority> grantedAuthorities = accessToken.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(SPRING_SECURITY_ROLE_PREFIX + role))
                .collect(Collectors.toList());

        UserDetails userDetails = new User(accessToken.getSubject(), "", grantedAuthorities);
        log.info("User Authorities in Security Context: {}" , userDetails.getAuthorities());

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


}

