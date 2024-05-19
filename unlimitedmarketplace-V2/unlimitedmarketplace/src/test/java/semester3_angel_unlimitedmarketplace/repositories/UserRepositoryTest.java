package semester3_angel_unlimitedmarketplace.repositories;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import semester3_angel_unlimitedmarketplace.persistence.UserRepository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest

public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void deleteUserById_whenUserExists() {
        // Setup: Assume a user with ID 1 exists
        Long userId = 1L; // Adjust based on your test setup
        userRepository.deleteById(userId);

        // Verify the user is no longer in the repository
        assertThat(userRepository.findById(userId)).isEmpty();
    }

    @Test
    void deleteUserById_whenUserDoesNotExist() {
        // Setup: Ensure no user with ID 99 exists
        Long userId = 99L;
        boolean existsBefore = userRepository.existsById(userId);
        userRepository.deleteById(userId);
        boolean existsAfter = userRepository.existsById(userId);

        // Verify that the user did not exist before and still does not exist after
        assertThat(existsBefore).isFalse();
        assertThat(existsAfter).isFalse();
    }


}
