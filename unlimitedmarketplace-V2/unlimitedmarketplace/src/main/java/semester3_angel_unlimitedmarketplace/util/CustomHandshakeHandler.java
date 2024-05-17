//package semester3_angel_unlimitedmarketplace.util;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
//
//import java.security.Key;
//import java.security.Principal;
//import java.util.Map;
//
//
//public class CustomHandshakeHandler extends DefaultHandshakeHandler {
//    private final Key key;
//
//    public CustomHandshakeHandler(@Value("jwt.secret") String key) {
//        byte[] keyBytes = Decoders.BASE64.decode(key);
//        this.key = Keys.hmacShaKeyFor(keyBytes);
//    }
//
//    @Override
//    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
//                                      Map<String, Object> attributes) {
//        // Extract token from query parameter
//        String token = request.getURI().getQuery().split("=")[1]; // Simplified; consider more robust parsing
//        if (token != null && !token.isEmpty()) {
//            // Assuming you have a method to validate the token and extract username
//            String username = validateAndExtractUsernameFromToken(token);
//            if (username != null) {
//                return new StompPrincipal(username);
//            }
//        }
//        return super.determineUser(request, wsHandler, attributes);
//    }
//
//    private String validateAndExtractUsernameFromToken(String token) {
//        try {
//            Claims claims = Jwts.parserBuilder()
//                    .setSigningKey(key) // Ensure this is the correct key
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody();
//            return claims.getSubject(); // Or another unique identifier
//        } catch (Exception e) {
//            System.out.println("Token validation error: " + e.getMessage());
//            return null;
//        }
//    }
//
//}
//
//
//
