package unlimitedmarketplace.repositories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import unlimitedmarketplace.persistence.entity.RefreshToken;
import unlimitedmarketplace.persistence.repositories.RefreshTokenRepository;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
 class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
     void testFindByToken() {
        // Arrange
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("testToken");
        refreshToken.setExpiryDate(Instant.now().plusSeconds(3600)); // Set expiry date to 1 hour in the future
        refreshToken.setUsername("testUser");
        refreshTokenRepository.save(refreshToken);

        // Act
        Optional<RefreshToken> foundToken = refreshTokenRepository.findByToken("testToken");

        // Assert
        assertTrue(foundToken.isPresent());
        assertEquals("testToken", foundToken.get().getToken());
    }

    @Test
     void testSaveAndFindById() {
        // Arrange
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("anotherTestToken");
        refreshToken.setExpiryDate(Instant.now().plusSeconds(3600)); // Set expiry date to 1 hour in the future
        refreshToken.setUsername("testUser");
        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);

        // Act
        Optional<RefreshToken> foundToken = refreshTokenRepository.findById(savedToken.getId());

        // Assert
        assertTrue(foundToken.isPresent());
        assertEquals("anotherTestToken", foundToken.get().getToken());
    }
}
