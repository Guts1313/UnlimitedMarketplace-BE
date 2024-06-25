package unlimitedmarketplace.security;

import org.springframework.stereotype.Service;
import unlimitedmarketplace.persistence.repositories.RefreshTokenRepository;
import unlimitedmarketplace.persistence.entity.RefreshToken;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String createRefreshToken(String username) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis((long) 7 * 24 * 60 * 60 * 1000)); // 7 days validity
        refreshToken.setUsername(username);
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    public boolean isValid(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(refreshToken -> !refreshToken.getExpiryDate().isBefore(Instant.now()))
                .orElse(false);
    }

    public String getUsernameFromRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(RefreshToken::getUsername)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
    }

    public void invalidateRefreshToken(String refreshToken) {
        Optional<RefreshToken> token = refreshTokenRepository.findByToken(refreshToken);
        if (token.isPresent()) {
            RefreshToken token1 = new RefreshToken();
            token1.setToken(token.toString());
            refreshTokenRepository.delete(token1);
        }
    }

}
