package unlimitedmarketplace.services;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import unlimitedmarketplace.business.impl.CreateProductUseCaseImpl;
import unlimitedmarketplace.domain.CreateProductRequest;
import unlimitedmarketplace.domain.CreateProductResponse;
import unlimitedmarketplace.persistence.entity.ProductEntity;
import unlimitedmarketplace.persistence.entity.UserEntity;
import unlimitedmarketplace.persistence.repositories.ProductRepository;
import unlimitedmarketplace.persistence.repositories.UserRepository;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
public class CreateProductUseCaseTest {
    @Test
     void testCreateProduct_UserExists() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ProductRepository productRepository = Mockito.mock(ProductRepository.class);
        CreateProductUseCaseImpl createProductUseCase = new CreateProductUseCaseImpl(productRepository, userRepository);

        Long userId = 1L;
        CreateProductRequest request = new CreateProductRequest();
        request.setUserId(userId);
        request.setProductName("testProduct");
        request.setProductPrice(10.0);
        request.setProductUrl("http://test.url");

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(1L);
        when(productRepository.save(any(ProductEntity.class))).thenReturn(productEntity);

        CreateProductResponse response = createProductUseCase.createProduct(request);

        assertEquals(1L, response.getId());
    }

    @Test
     void testCreateProduct_UserDoesNotExist() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ProductRepository productRepository = Mockito.mock(ProductRepository.class);
        CreateProductUseCaseImpl createProductUseCase = new CreateProductUseCaseImpl(productRepository, userRepository);

        Long userId = 1L;
        CreateProductRequest request = new CreateProductRequest();
        request.setUserId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> createProductUseCase.createProduct(request));
    }
}


