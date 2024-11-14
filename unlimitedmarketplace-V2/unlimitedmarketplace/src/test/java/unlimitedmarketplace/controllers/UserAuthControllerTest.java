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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import unlimitedmarketplace.domain.UserService;
import unlimitedmarketplace.persistence.entity.UserEntity;
import unlimitedmarketplace.security.AccessTokenEncoderDecoderImpl;
import unlimitedmarketplace.security.RefreshTokenServiceImpl;
import unlimitedmarketplace.util.JwtUtil;

import java.util.Collections;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
 class UserAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private AuthenticationManager authenticationManager;

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
     void testAuthenticateUser() throws Exception {
        String username = "gosu";
        String passwordHash = "testpass";

        UserEntity mockUser = new UserEntity();
        mockUser.setId(1L);
        mockUser.setUserName(username);

        when(userService.findByUsername(username)).thenReturn(mockUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, Collections.singletonList(new SimpleGrantedAuthority("USER")));
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(tokenService.encodeAndGetId(anyString(), anyLong(), anyCollection())).thenReturn("dummyToken");

        mockMvc.perform(post("/unlimitedmarketplace/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"" + username + "\", \"passwordHash\": \"" + passwordHash + "\"}"))
                .andExpect(status().isOk());
    }

    @Test
     void testAuthenticateUserReturnsJwtToken() throws Exception {
        // Arrange
        String username = "gosu";
        String passwordHash = "testpass";
        String expectedToken = "expectedDummyToken";

        UserEntity mockUser = new UserEntity();
        mockUser.setId(1L);
        mockUser.setUserName(username);

        when(userService.findByUsername(username)).thenReturn(mockUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, Collections.singletonList(new SimpleGrantedAuthority("USER")));
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);

        when(tokenService.encodeAndGetId(anyString(), anyLong(), anyCollection())).thenReturn(expectedToken);
        mockMvc.perform(post("/unlimitedmarketplace/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\", \"passwordHash\":\"" + passwordHash + "\"}"))
                .andExpect(status().isOk())
                .andDo(print());
    }


    @Test
     void testRefreshTokenInvalid() throws Exception {
        String refreshToken = "invalidRefreshToken";

        when(refreshTokenService.isValid(refreshToken)).thenReturn(false);

        mockMvc.perform(post("/unlimitedmarketplace/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\": \"" + refreshToken + "\"}"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }


}
