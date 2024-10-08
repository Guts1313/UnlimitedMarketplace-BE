package unlimitedmarketplace.repositories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import unlimitedmarketplace.domain.UserRoles;
import unlimitedmarketplace.persistence.entity.*;
import unlimitedmarketplace.persistence.repositories.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
 class EntityTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Test
     void testTransactionEntity() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setUserName("testUser");
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword");
        user.setUserRole(UserRoles.USER);
        user = userRepository.save(user);

        TransactionEntity transaction = TransactionEntity.builder()
                .user(user)
                .cardType("VISA")
                .cardNumber("1234")
                .amount(100.0)
                .timestamp(LocalDateTime.now())
                .status("COMPLETED")
                .build();

        // Act
        TransactionEntity savedTransaction = transactionRepository.save(transaction);
        Optional<TransactionEntity> foundTransaction = transactionRepository.findById(savedTransaction.getId());

        // Assert
        assertTrue(foundTransaction.isPresent());
        assertEquals("1234", foundTransaction.get().getCardNumber());
        assertEquals(100.0, foundTransaction.get().getAmount());
        assertEquals("COMPLETED", foundTransaction.get().getStatus());
    }

    @Test
     void testPaymentMethodEntity() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setUserName("testUser");
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword");
        user.setUserRole(UserRoles.USER);
        user = userRepository.save(user);

        PaymentMethodEntity paymentMethod = PaymentMethodEntity.builder()
                .user(user)
                .cardType("VISA")
                .cardNumber("1234")
                .cardName("Test User")
                .expirationDate("12/25")
                .encryptedCvv("encryptedCvv")
                .build();

        // Act
        PaymentMethodEntity savedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        Optional<PaymentMethodEntity> foundPaymentMethod = paymentMethodRepository.findById(savedPaymentMethod.getId());

        // Assert
        assertTrue(foundPaymentMethod.isPresent());
        assertEquals("1234", foundPaymentMethod.get().getCardNumber());
        assertEquals("Test User", foundPaymentMethod.get().getCardName());
        assertEquals("12/25", foundPaymentMethod.get().getExpirationDate());
        assertEquals("encryptedCvv", foundPaymentMethod.get().getEncryptedCvv());
    }
    @Test
     void testRefreshTokenEntity() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("testToken");
        refreshToken.setExpiryDate(Instant.now().plusSeconds(3600));
        refreshToken.setUsername("testUser");
        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        Optional<RefreshToken> foundToken = refreshTokenRepository.findByToken("testToken");
        assertTrue(foundToken.isPresent());
        assertEquals("testToken", foundToken.get().getToken());
        assertEquals("testUser", foundToken.get().getUsername());
    }

    @Test
     void testProductEntity() {
        UserEntity user = createUser();
        ProductEntity product = ProductEntity.builder()
                .productName("Test Product")
                .productPrice(50.0)
                .productUrl("http://example.com")
                .productDateAdded("2024-01-01")
                .productStatus("ACTIVE")
                .paymentStatus("PENDING")
                .user(user)
                .build();
        ProductEntity savedProduct = productRepository.save(product);
        Optional<ProductEntity> foundProduct = productRepository.findById(savedProduct.getId());
        assertTrue(foundProduct.isPresent());
        assertEquals("Test Product", foundProduct.get().getProductName());
        assertEquals(50.0, foundProduct.get().getProductPrice());
        assertEquals("ACTIVE", foundProduct.get().getProductStatus());
    }

    @Test
     void testBidEntity() {
        UserEntity user = createUser();
        ProductEntity product = createProduct(user);
        BidEntity bid = BidEntity.builder()
                .amount(BigDecimal.valueOf(100))
                .bidTime(LocalDateTime.now())
                .bidStatus("PENDING")
                .product(product)
                .user(user)
                .build();
        BidEntity savedBid = bidRepository.save(bid);
        Optional<BidEntity> foundBid = bidRepository.findById(savedBid.getId());
        assertTrue(foundBid.isPresent());
        assertEquals(BigDecimal.valueOf(100), foundBid.get().getAmount());
        assertEquals("PENDING", foundBid.get().getBidStatus());
    }

    @Test
     void testSubscriptionEntity() {
        SubscriptionEntity subscription = SubscriptionEntity.builder()
                .userId(1L)
                .channel("testChannel")
                .build();
        SubscriptionEntity savedSubscription = subscriptionRepository.save(subscription);
        Optional<SubscriptionEntity> foundSubscription = subscriptionRepository.findById(savedSubscription.getId());
        assertTrue(foundSubscription.isPresent());
        assertEquals(1L, foundSubscription.get().getUserId());
        assertEquals("testChannel", foundSubscription.get().getChannel());
    }

     UserEntity createUser() {
        UserEntity user = UserEntity.builder()
                .userName("testUser")
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .role(UserRoles.USER)
                .build();
        return userRepository.save(user);
    }

     ProductEntity createProduct(UserEntity user) {
        ProductEntity product = ProductEntity.builder()
                .productName("Test Product")
                .productPrice(50.0)
                .productUrl("http://example.com")
                .productDateAdded("2024-01-01")
                .productStatus("ACTIVE")
                .paymentStatus("PENDING")
                .user(user)
                .build();
        return productRepository.save(product);
    }
}


