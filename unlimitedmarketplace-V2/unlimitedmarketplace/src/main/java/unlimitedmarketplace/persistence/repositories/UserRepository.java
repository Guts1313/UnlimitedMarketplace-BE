package unlimitedmarketplace.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import unlimitedmarketplace.persistence.entity.UserEntity;

import java.util.List;
import java.util.Optional;


public interface UserRepository  extends JpaRepository<UserEntity,Long> {
    List<UserEntity> findAllByUserName(String userName);
    Optional<UserEntity> findByUserName(String userName);
    Optional<UserEntity> findByEmail(String email);

}
