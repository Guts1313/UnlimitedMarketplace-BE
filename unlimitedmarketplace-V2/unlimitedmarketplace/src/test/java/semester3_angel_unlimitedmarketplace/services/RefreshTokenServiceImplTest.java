package semester3_angel_unlimitedmarketplace.services;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import semester3_angel_unlimitedmarketplace.persistence.RefreshTokenRepository;
import semester3_angel_unlimitedmarketplace.persistence.entity.RefreshToken;
import semester3_angel_unlimitedmarketplace.security.RefreshTokenServiceImpl;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
    public class RefreshTokenServiceImplTest {

        @MockBean
        private RefreshTokenRepository refreshTokenRepository;

        @Autowired
        private RefreshTokenServiceImpl refreshTokenService;

        @Test
        public void testCreateRefreshToken() {
            String username = "user";
            Mockito.when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArguments()[0]);

            String token = refreshTokenService.createRefreshToken(username);
            assertNotNull(token);
        }

        @Test
        public void testIsValid() {
            RefreshToken token = new RefreshToken();
            token.setToken("token123");
            token.setExpiryDate(Instant.now().plusSeconds(3600)); // 1 hour from now
            Mockito.when(refreshTokenRepository.findByToken("token123")).thenReturn(Optional.of(token));

            assertTrue(refreshTokenService.isValid("token123"));
        }

    }
