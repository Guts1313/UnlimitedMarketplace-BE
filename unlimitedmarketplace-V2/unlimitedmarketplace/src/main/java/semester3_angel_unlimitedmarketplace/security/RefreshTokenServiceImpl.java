package semester3_angel_unlimitedmarketplace.security;

import org.springframework.stereotype.Service;
import semester3_angel_unlimitedmarketplace.persistence.RefreshTokenRepository;
import semester3_angel_unlimitedmarketplace.persistence.entity.RefreshToken;

import java.time.Instant;
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
        refreshToken.setExpiryDate(Instant.now().plusMillis(7 * 24 * 60 * 60 * 1000)); // 7 days validity
        refreshToken.setUsername(username);
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    public boolean isValid(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(refreshToken -> !refreshToken.getExpiryDate().isBefore(Instant.now()))
                .orElse(false);
    }

    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
    public String getUsernameFromRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(RefreshToken::getUsername)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
    }


}
