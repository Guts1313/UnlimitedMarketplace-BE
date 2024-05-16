package semester3_angel_unlimitedmarketplace.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import semester3_angel_unlimitedmarketplace.domain.AuthResponse;
import semester3_angel_unlimitedmarketplace.domain.LoginRequest;
import semester3_angel_unlimitedmarketplace.domain.LoginResponse;
import semester3_angel_unlimitedmarketplace.domain.UserService;
import semester3_angel_unlimitedmarketplace.security.AccessToken;
import semester3_angel_unlimitedmarketplace.security.AccessTokenEncoderDecoderImpl;
import semester3_angel_unlimitedmarketplace.security.AccessTokenImpl;
import semester3_angel_unlimitedmarketplace.security.RefreshTokenServiceImpl;
import semester3_angel_unlimitedmarketplace.util.JwtUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/unlimitedmarketplace/auth")
public class UserAuthController {

    private final AuthenticationManager authenticationManager;
    private final AccessTokenEncoderDecoderImpl tokenService;
    private final RefreshTokenServiceImpl refreshTokenService;
    private final UserService userService; // Added UserService

    public UserAuthController(AuthenticationManager authenticationManager, AccessTokenEncoderDecoderImpl tokenService,
                              RefreshTokenServiceImpl refreshTokenService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.refreshTokenService = refreshTokenService;
        this.userService = userService; // Initialize in constructor
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPasswordHash()
                    )
            );

            if (authentication == null || !authentication.isAuthenticated()) {
                System.out.println("Authentication failed.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenService.encode(authentication.getName(), authentication.getAuthorities());
            String refreshToken = refreshTokenService.createRefreshToken(loginRequest.getUsername()); // Generate and store refresh token

            System.out.println("Generated JWT: " + jwt); // Check the JWT output

            if (jwt == null) {
                System.out.println("JWT is null, check tokenService and key configuration.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            // Return both tokens in the response
            LoginResponse response = new LoginResponse(jwt, refreshToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Error during authentication: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (!refreshTokenService.isValid(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
        }

        try {
            String username = refreshTokenService.getUsernameFromRefreshToken(refreshToken);
            List<SimpleGrantedAuthority> authorities = userService.getAuthoritiesByUsername(username);

            Authentication newAuth = new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            String newAccessToken = tokenService.encode(newAuth.getName(), authorities);
            String newRefreshToken = refreshTokenService.createRefreshToken(username);  // Rotate the refresh token

            return ResponseEntity.ok(new AuthResponse(newAccessToken, newRefreshToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing refresh token: " + e.getMessage());
        }
    }
}
