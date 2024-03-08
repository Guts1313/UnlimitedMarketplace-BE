package semester3_angel_unlimitedmarketplace.controllers;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import semester3_angel_unlimitedmarketplace.domain.LoginRequest;
import semester3_angel_unlimitedmarketplace.domain.LoginResponse;
import semester3_angel_unlimitedmarketplace.security.AccessToken;
import semester3_angel_unlimitedmarketplace.security.AccessTokenEncoderDecoderImpl;
import semester3_angel_unlimitedmarketplace.security.AccessTokenImpl;
import semester3_angel_unlimitedmarketplace.util.JwtUtil;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:3000") // Replace with your React application's URL
@RequestMapping("/unlimitedmarketplace/auth")
public class UserAuthController {

    private final AuthenticationManager authenticationManager;
    private final AccessTokenEncoderDecoderImpl tokenService;  // Using the custom token service

    public UserAuthController(AuthenticationManager authenticationManager, AccessTokenEncoderDecoderImpl tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:3000") // Replace with your React application's URL
    public ResponseEntity<LoginResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPasswordHash()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenService.encode(createAccessToken(authentication));  // Encode the token

        return ResponseEntity.ok(new LoginResponse(jwt));
    }

    private AccessToken createAccessToken(Authentication authentication) {
        Set<String> roles = authentication.getAuthorities().stream()
                .map(grantedAuthority -> ((SimpleGrantedAuthority) grantedAuthority).getAuthority())
                .collect(Collectors.toSet());

        return new AccessTokenImpl(authentication.getName(), null, roles);  // No studentId
    }



}

