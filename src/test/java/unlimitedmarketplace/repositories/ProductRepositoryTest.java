package unlimitedmarketplace.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import unlimitedmarketplace.domain.UserRoles;
import unlimitedmarketplace.persistence.repositories.ProductRepository;
import unlimitedmarketplace.persistence.repositories.UserRepository;
import unlimitedmarketplace.persistence.entity.ProductEntity;
import unlimitedmarketplace.persistence.entity.UserEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
 class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    private UserEntity user;
    private ProductEntity product;

    @BeforeEach
     void setUp() {
        user = UserEntity.builder()
                .userName("testuser")
                .passwordHash("testpassword")
                .email("test@example.com")
                .role(UserRoles.USER)
                .build();

        // Save the user first
        user = userRepository.save(user);

        product = ProductEntity.builder()
                .productName("Test Product")
                .productPrice(100.0)
                .productStatus("AVAILABLE")
                .paymentStatus("PENDING")
                .user(user) // Associate the product with the saved user
                .build();

        // Save the product
        productRepository.save(product);
    }

    @Test
     void testFindProductEntitiesByProductNameLike() {
        List<ProductEntity> products = productRepository.findProductEntitiesByProductNameLike("Test%");
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getProductName()).isEqualTo("Test Product");
    }

    @Test
     void testFindListedByUserId() {
        List<ProductEntity> products = productRepository.findListedByUserId(user.getId());
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getUser().getUserName()).isEqualTo("testuser");
    }

    @Test
     void testFindAllByProductStatus() {
        List<ProductEntity> products = productRepository.findAllByProductStatus("AVAILABLE");
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getProductStatus()).isEqualTo("AVAILABLE");
    }

    @Test
     void testFindAllByPaymentStatus() {
        List<ProductEntity> products = productRepository.findAllByPaymentStatus("PENDING");
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getPaymentStatus()).isEqualTo("PENDING");
    }

    @Test
     void testFindProductEntityById() {
        ProductEntity foundProduct = productRepository.findProductEntityById(product.getId());
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getProductName()).isEqualTo("Test Product");
    }
}
