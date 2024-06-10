package unlimitedmarketplace.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import unlimitedmarketplace.persistence.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long>{
    Optional<RefreshToken> findByToken(String token);
}
