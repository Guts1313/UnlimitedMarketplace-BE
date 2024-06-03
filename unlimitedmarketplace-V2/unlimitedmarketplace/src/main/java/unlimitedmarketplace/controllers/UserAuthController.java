package unlimitedmarketplace.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import unlimitedmarketplace.domain.*;
import unlimitedmarketplace.persistence.entity.UserEntity;
import unlimitedmarketplace.security.AccessToken;
import unlimitedmarketplace.security.AccessTokenEncoderDecoderImpl;
import unlimitedmarketplace.security.RefreshTokenServiceImpl;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/unlimitedmarketplace/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class UserAuthController {

    private final AuthenticationManager authenticationManager;
    private final AccessTokenEncoderDecoderImpl tokenService;
    private final RefreshTokenServiceImpl refreshTokenService;
    private final UserService userService; // Added UserService
    private static final Logger logs = LoggerFactory.getLogger(UserAuthController.class);

    public UserAuthController(AuthenticationManager authenticationManager, AccessTokenEncoderDecoderImpl tokenService,
                              RefreshTokenServiceImpl refreshTokenService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
    }

    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:3000")

    public ResponseEntity<LoginResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPasswordHash()
                    )
            );

            if (authentication == null || !authentication.isAuthenticated()) {
                logs.info("%Authentication failed.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserEntity user = userService.findByUsername(loginRequest.getUsername());
            Long userId = user.getId();
            String jwt = tokenService.encodeAndGetId(authentication.getName(), userId, authentication.getAuthorities());
            String refreshToken = refreshTokenService.createRefreshToken(loginRequest.getUsername()); // Generate and store refresh token
            AccessToken token = tokenService.decodeEncoded(jwt);
            logs.info("Acc tok: {}" , token);
            logs.info("UID: {}" , userId);
            logs.info("Generated JWT: {}" , jwt);

            if (jwt == null) {
                logs.info("J%WT is null, %check tokenService and key configuration.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            // Return both tokens in the response
            LoginResponse response = new LoginResponse(jwt, refreshToken, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logs.info("Error during authentication: {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (!refreshTokenService.isValid(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String username = refreshTokenService.getUsernameFromRefreshToken(refreshToken);
            UserEntity user = userService.findByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            List<SimpleGrantedAuthority> authorities = userService.getAuthoritiesByUsername(username);
            // Authenticate with the new authorities
            Authentication newAuth = new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            Long userId = user.getId();
            String newAccessToken = tokenService.encodeAndGetId(newAuth.getName(), userId, newAuth.getAuthorities());
            String newRefreshToken = refreshTokenService.createRefreshToken(username);  // Optionally rotate the refresh token

            return ResponseEntity.ok(new AuthResponse(newAccessToken, newRefreshToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> body) {
        try {

            String refreshToken = body.get("refreshToken");
            if (!refreshTokenService.isValid(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            refreshTokenService.invalidateRefreshToken(refreshToken);
            return ResponseEntity.ok("Logout successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Logout failed");
        }
    }


}
