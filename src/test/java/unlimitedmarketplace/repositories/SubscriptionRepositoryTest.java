package unlimitedmarketplace.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import unlimitedmarketplace.domain.UserRoles;
import unlimitedmarketplace.persistence.repositories.SubscriptionRepository;
import unlimitedmarketplace.persistence.repositories.UserRepository;
import unlimitedmarketplace.persistence.entity.SubscriptionEntity;
import unlimitedmarketplace.persistence.entity.UserEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
 class SubscriptionRepositoryTest {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    private UserEntity user;

    @BeforeEach
     void setUp() {
        // Create and save the user
        user = UserEntity.builder()
                .userName("testUser")
                .email("test@example.com")
                .passwordHash("password")
                .role(UserRoles.USER)
                .build();
        user = userRepository.save(user);

        // Create and save the subscriptions
        SubscriptionEntity subscription1 = SubscriptionEntity.builder()
                .userId(user.getId())
                .channel("channel1")
                .build();
        subscriptionRepository.save(subscription1);

        SubscriptionEntity subscription2 = SubscriptionEntity.builder()
                .userId(user.getId())
                .channel("channel2")
                .build();
        subscriptionRepository.save(subscription2);
    }

    @Test
     void testFindByUserId() {
        // Retrieve subscriptions by userId
        List<SubscriptionEntity> subscriptions = subscriptionRepository.findByUserId(user.getId());
        // Assert that the size of the retrieved subscriptions is 2
        assertThat(subscriptions).hasSize(2);
    }
}
