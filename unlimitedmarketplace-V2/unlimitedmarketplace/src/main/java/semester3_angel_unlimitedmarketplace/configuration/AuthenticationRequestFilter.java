package semester3_angel_unlimitedmarketplace.configuration;

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
import semester3_angel_unlimitedmarketplace.security.AccessToken;
import semester3_angel_unlimitedmarketplace.security.AccessTokenDecoder;
import semester3_angel_unlimitedmarketplace.security.AccessTokenEncoderDecoderImpl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class AuthenticationRequestFilter extends OncePerRequestFilter {

    private static final String SPRING_SECURITY_ROLE_PREFIX = "ROLE_";
    private static final Logger log = LoggerFactory.getLogger(AccessTokenEncoderDecoderImpl.class);

    @Autowired
    private AccessTokenDecoder accessTokenDecoder;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");
        if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String accessTokenString = requestTokenHeader.substring(7);

        try {
            AccessToken accessToken = accessTokenDecoder.decode(accessTokenString);
            setupSpringSecurityContext(accessToken);
            chain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("Error validating access token", e);
            sendAuthenticationError(response);
        }
    }

    private void sendAuthenticationError(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.flushBuffer();
    }

    private void setupSpringSecurityContext(AccessToken accessToken) {
        // Map each role string to a SimpleGrantedAuthority object
        List<GrantedAuthority> grantedAuthorities = accessToken.getRoles()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // Use these authorities when creating the UserDetails
        UserDetails userDetails = new User(accessToken.getSubject(), "", grantedAuthorities);

        log.info("User Authorities in Security Context: " + userDetails.getAuthorities());  // This should now correctly reflect the authorities

        // Setup the security context with the UserDetails
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }


}

