package unlimitedmarketplace.controllers;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import unlimitedmarketplace.domain.UserRoles;
import unlimitedmarketplace.domain.UserService;
import unlimitedmarketplace.persistence.entity.UserEntity;
import unlimitedmarketplace.security.AccessTokenEncoderDecoderImpl;
import unlimitedmarketplace.security.RefreshTokenServiceImpl;
import unlimitedmarketplace.util.JwtUtil;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
@ActiveProfiles("test")  // Ensure this profile configures the necessary beans and settings for tests
 class UserAuthControllerTest {

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

    private static final Logger log = LoggerFactory.getLogger(UserAuthControllerTest.class);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testAuthenticateUser() throws Exception {
        String username = "gosu";
        String passwordHash = "testpass";

        // Create a mock UserEntity
        UserEntity mockUser = new UserEntity();
        mockUser.setId(1L);  // Assuming the ID is a Long
        mockUser.setUserName(username);

        // Mock UserService to return the mock user
        when(userService.findByUsername(username)).thenReturn(mockUser);

        // Mock authentication
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, Collections.singletonList(new SimpleGrantedAuthority("USER")));
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(tokenService.encodeAndGetId(anyString(), anyLong(), anyCollection())).thenReturn("dummyToken");

        mockMvc.perform(post("/unlimitedmarketplace/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"" + username + "\", \"passwordHash\": \"" + passwordHash + "\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testAuthenticateUserReturnsJwtToken() throws Exception {
        // Arrange
        String username = "gosu";
        String passwordHash = "testpass";
        String expectedToken = "expectedDummyToken";

        UserEntity mockUser = new UserEntity();
        mockUser.setId(1L);  // Assuming the ID is a Long
        mockUser.setUserName(username);

        // Mock UserService to return the mock user
        when(userService.findByUsername(username)).thenReturn(mockUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, Collections.singletonList(new SimpleGrantedAuthority("USER")));
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);

        when(tokenService.encodeAndGetId(anyString(), anyLong(), anyCollection())).thenReturn(expectedToken);
        // Act & Assert
        mockMvc.perform(post("/unlimitedmarketplace/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\", \"passwordHash\":\"" + passwordHash + "\"}"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testRefreshTokenValid() throws Exception {
        String refreshToken = "validRefreshToken";
        String username = "user";
        String newAccessToken = "newAccessToken";
        String newRefreshToken = "newRefreshToken";

        when(refreshTokenService.isValid(refreshToken)).thenReturn(true);
        when(refreshTokenService.getUsernameFromRefreshToken(refreshToken)).thenReturn(username);
        when(userService.getAuthoritiesByUsername(username)).thenReturn(List.of(new SimpleGrantedAuthority(UserRoles.USER.toString())));

        Authentication newAuth = new UsernamePasswordAuthenticationToken(username, null, List.of(new SimpleGrantedAuthority(UserRoles.USER.toString())));
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        when(tokenService.encode(username, List.of(new SimpleGrantedAuthority(UserRoles.USER.toString())))).thenReturn(newAccessToken);
        when(refreshTokenService.createRefreshToken(username)).thenReturn(newRefreshToken);

        mockMvc.perform(post("/unlimitedmarketplace/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\": \"" + refreshToken + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(newAccessToken))
                .andExpect(jsonPath("$.refreshToken").value(newRefreshToken))
                .andDo(print());
    }

    @Test
    public void testRefreshTokenInvalid() throws Exception {
        String refreshToken = "invalidRefreshToken";

        when(refreshTokenService.isValid(refreshToken)).thenReturn(false);

        mockMvc.perform(post("/unlimitedmarketplace/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\": \"" + refreshToken + "\"}"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }


}
