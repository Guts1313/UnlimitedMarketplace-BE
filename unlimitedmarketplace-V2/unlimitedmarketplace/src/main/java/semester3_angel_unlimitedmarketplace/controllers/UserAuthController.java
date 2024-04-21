package semester3_angel_unlimitedmarketplace.controllers;

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
import semester3_angel_unlimitedmarketplace.util.JwtUtil;

@RestController
@CrossOrigin(origins = "http://localhost:3000") // Replace with your React application's URL
@RequestMapping("/unlimitedmarketplace/auth")
public class UserAuthController {


    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    public UserAuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
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
        String jwt = jwtUtil.generateToken(authentication);

        return ResponseEntity.ok(new LoginResponse(jwt));
    }

}

