package semester3_angel_unlimitedmarketplace.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import semester3_angel_unlimitedmarketplace.persistence.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserRepository  extends JpaRepository<UserEntity,Long> {
    List<UserEntity> findAllByUserName(String userName);
    Optional<UserEntity> findByUserName(String userName);
    Optional<UserEntity> findByEmail(String email);

}
