package semester3_angel_unlimitedmarketplace.controllers;




import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import semester3_angel_unlimitedmarketplace.domain.UserService;
import semester3_angel_unlimitedmarketplace.security.AccessTokenEncoderDecoderImpl;
import semester3_angel_unlimitedmarketplace.security.RefreshTokenServiceImpl;
import semester3_angel_unlimitedmarketplace.util.JwtUtil;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
// Correct import for the post method
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private RefreshTokenServiceImpl refreshTokenService;

    @MockBean
    private AccessTokenEncoderDecoderImpl tokenService;

    @MockBean
    private UserService userService;




    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testAuthenticateUser() throws Exception {
        mockMvc.perform(post("/unlimitedmarketplace/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"gosu\", \"passwordHash\": \"testpass\"}"))
                .andExpect(status().isOk());
    }
    @Test
    public void testAuthenticateUserReturnsJwtToken() throws Exception {
        String expectedToken = "dummyToken";
        String username = "new";
        String password = "new";

        // Create a mock Authentication object
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        // Ensure the AuthenticationManager returns this mock object
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        // Mock the token service to return a specific token
        when(tokenService.encode(username, authentication.getAuthorities())).thenReturn(expectedToken);

        mockMvc.perform(post("/unlimitedmarketplace/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(expectedToken))
                .andDo(print()); // This line will print the response
    }

    @Test
    public void testRefreshTokenValid() throws Exception {
        String refreshToken = "validRefreshToken";
        Mockito.when(refreshTokenService.isValid(refreshToken)).thenReturn(true);
        Mockito.when(refreshTokenService.getUsernameFromRefreshToken(refreshToken)).thenReturn("user");
        Mockito.when(userService.getAuthoritiesByUsername("user")).thenReturn(List.of(new SimpleGrantedAuthority("ROLE_USER")));
        Mockito.when(tokenService.encode(anyString(), anyList())).thenReturn("newAccessToken");

        mockMvc.perform(post("/unlimitedmarketplace/auth/refresh-token")
                        .param("refreshToken", refreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("newAccessToken"));
    }

    @Test
    public void testRefreshTokenInvalid() throws Exception {
        String refreshToken = "invalidRefreshToken";
        Mockito.when(refreshTokenService.isValid(refreshToken)).thenReturn(false);

        mockMvc.perform(post("/unlimitedmarketplace/auth/refresh-token")
                        .param("refreshToken", refreshToken))
                .andExpect(status().isUnauthorized());
    }



}
