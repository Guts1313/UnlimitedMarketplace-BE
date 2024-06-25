package unlimitedmarketplace.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import unlimitedmarketplace.domain.UserRoles;
import unlimitedmarketplace.persistence.repositories.PaymentMethodRepository;
import unlimitedmarketplace.persistence.repositories.UserRepository;
import unlimitedmarketplace.persistence.entity.PaymentMethodEntity;
import unlimitedmarketplace.persistence.entity.UserEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
 class PaymentMethodRepositoryTest {

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

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

        // Create and save payment methods
        PaymentMethodEntity paymentMethod1 = PaymentMethodEntity.builder()
                .user(user)
                .cardType("visa")
                .cardNumber("**** **** **** 1234")
                .cardName("John Doe")
                .expirationDate("12/24")
                .encryptedCvv("encryptedCvv1")
                .build();
        paymentMethodRepository.save(paymentMethod1);

        PaymentMethodEntity paymentMethod2 = PaymentMethodEntity.builder()
                .user(user)
                .cardType("mastercard")
                .cardNumber("**** **** **** 5678")
                .cardName("Jane Doe")
                .expirationDate("11/23")
                .encryptedCvv("encryptedCvv2")
                .build();
        paymentMethodRepository.save(paymentMethod2);
    }

    @Test
     void testFindByUserId() {
        // Retrieve payment methods by userId
        List<PaymentMethodEntity> paymentMethods = paymentMethodRepository.findByUserId(user.getId());
        // Assert that the size of the retrieved payment methods is 2
        assertThat(paymentMethods).hasSize(2);

        // Verify the details of the payment methods
        assertThat(paymentMethods.get(0).getCardType()).isEqualTo("visa");
        assertThat(paymentMethods.get(1).getCardType()).isEqualTo("mastercard");
    }
}
