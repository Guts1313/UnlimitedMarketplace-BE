package unlimitedmarketplace.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import unlimitedmarketplace.persistence.entity.UserEntity;

import java.util.List;
import java.util.Optional;


public interface UserRepository  extends JpaRepository<UserEntity,Long> {
    @Query("SELECT u FROM UserEntity u WHERE u.userName = :userName")
    List<UserEntity> findAllByUserName(@Param("userName") String userName);

    @Query("SELECT u FROM UserEntity u WHERE u.userName = :userName")
    Optional<UserEntity> findByUserName(@Param("userName") String userName);

    @Query("SELECT u FROM UserEntity u WHERE u.email = :email")
    Optional<UserEntity>findByEmail(@Param("email") String email);

}
