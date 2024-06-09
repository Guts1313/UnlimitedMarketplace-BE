package unlimitedmarketplace.configuration;

import io.jsonwebtoken.Claims;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import unlimitedmarketplace.security.AccessTokenEncoderDecoderImpl;

import java.security.Principal;
import java.util.*;


@Configuration
@EnableWebSocketMessageBroker
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {


    private final AccessTokenEncoderDecoderImpl tokenDecoder;
    private static final Logger log = LoggerFactory.getLogger(StompWebSocketConfig.class);

    public StompWebSocketConfig(AccessTokenEncoderDecoderImpl tokenDecoder) {
        this.tokenDecoder = tokenDecoder;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket-sockjs-stomp")
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                        String authToken = extractToken(request);
                        if (authToken == null || authToken.isEmpty()) {
                            log.error("JWT token is missing or empty");
                            return null;
                        }
                        try {
                            Claims claims = tokenDecoder.decode(authToken);
                            String username = claims.getSubject();
                            Collection<? extends GrantedAuthority> authorities = getAuthorities(claims);
                            log.info("Stomp endpoint>>>Decoded username: {}, authorities: {}", username, authorities);
                            return new UsernamePasswordAuthenticationToken(username, null, authorities);
                        } catch (Exception e) {
                            log.error("Stomp endpoint>>>Error decoding JWT token: {}", e.getMessage(), e);
                            return null;
                        }
                    }


                })
                .setAllowedOrigins("https://sem3-fe-frontend-myvoxyxc3a-lz.a.run.app")
                .withSockJS();
    }

    private String extractToken(ServerHttpRequest request) {
        String query = request.getURI().getQuery();
        return Arrays.stream(query.split("&"))
                .filter(param -> param.startsWith("access_token="))
                .findFirst()
                .map(param -> param.substring("access_token=".length()))
                .orElse(null);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Claims claims) {
        List<String> roles = claims.get("roles", List.class);
        return List.copyOf(roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.toUpperCase()))
                .toList());
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue", "/topic");
        registry.setUserDestinationPrefix("/user");  // Ensure this is set if using convertAndSendToUser
        registry.setApplicationDestinationPrefixes("/app");
    }
}








