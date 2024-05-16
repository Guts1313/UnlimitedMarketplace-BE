package semester3_angel_unlimitedmarketplace.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import semester3_angel_unlimitedmarketplace.persistence.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long>{
    Optional<RefreshToken> findByToken(String token);
    void deleteByToken(String token);

}
