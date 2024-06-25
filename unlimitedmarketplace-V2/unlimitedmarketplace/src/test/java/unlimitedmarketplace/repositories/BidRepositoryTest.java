package unlimitedmarketplace.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import unlimitedmarketplace.domain.UserRoles;
import unlimitedmarketplace.persistence.repositories.BidRepository;
import unlimitedmarketplace.persistence.repositories.ProductRepository;
import unlimitedmarketplace.persistence.repositories.UserRepository;
import unlimitedmarketplace.persistence.entity.BidEntity;
import unlimitedmarketplace.persistence.entity.ProductEntity;
import unlimitedmarketplace.persistence.entity.UserEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
 class BidRepositoryTest {

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    private UserEntity user;
    private ProductEntity product;

    @BeforeEach
     void setUp() {
        // Create and save a user
        user = UserEntity.builder()
                .userName("testUser")
                .email("test@example.com")
                .passwordHash("password")
                .role(UserRoles.USER)  // Ensure you have the correct role defined
                .build();
        user = userRepository.save(user);

        // Create and save a product
        product = ProductEntity.builder()
                .productName("testProduct")
                .productPrice(Double.valueOf(100))
                .productStatus("AVAILABLE")
                .paymentStatus("PENDING")
                .productDateAdded(LocalDateTime.now().toString())
                .user(user)  // Set the user who added the product
                .build();
        product = productRepository.save(product);

        // Create and save bids
        BidEntity bid1 = BidEntity.builder()
                .user(user)
                .product(product)
                .amount(BigDecimal.valueOf(100))
                .bidTime(LocalDateTime.now())
                .bidStatus("SENT")
                .build();
        bidRepository.save(bid1);

        BidEntity bid2 = BidEntity.builder()
                .user(user)
                .product(product)
                .amount(BigDecimal.valueOf(200))
                .bidTime(LocalDateTime.now())
                .bidStatus("ACCEPTED")
                .build();
        bidRepository.save(bid2);
    }

    @Test
     void testFindHighestBidByProductId() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<BidEntity> highestBid = bidRepository.findHighestBidByProductId(product.getId(), pageable);
        assertThat(highestBid.getContent()).hasSize(1);
        assertThat(highestBid.getContent().get(0).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(200));
    }

    @Test
     void testFindAllBiddersExceptLatest() {
        List<String> bidders = bidRepository.findAllBiddersExceptLatest(product.getId(), "testUser");
        assertThat(bidders).isEmpty();
    }

    @Test
     void testFindProductsByUserId() {
        List<ProductEntity> products = bidRepository.findProductsByUserId(user.getId());
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getProductName()).isEqualTo("testProduct");
    }

    @Test
     void testFindBiddedProductsByUserId() {
        List<BidEntity> bids = bidRepository.findBiddedProductsByUserId(user.getId());
        assertThat(bids).hasSize(2);
    }

    @Test
     void testFindFirstByAmountAndUserIdOrderByBidTimeDesc() {
        BidEntity bid = bidRepository.findFirstByAmountAndUserIdOrderByBidTimeDesc(BigDecimal.valueOf(100), user.getId());
        assertThat(bid).isNotNull();
        assertThat(bid.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(100));
    }
}

