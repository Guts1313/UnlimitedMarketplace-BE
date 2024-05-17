package unlimitedmarketplace.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import unlimitedmarketplace.business.impl.GetProductUseCaseImpl;
import unlimitedmarketplace.domain.GetProductRequest;
import unlimitedmarketplace.domain.GetProductResponse;
import unlimitedmarketplace.persistence.repositories.ProductRepository;
import unlimitedmarketplace.persistence.entity.ProductEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

  class GetProductUseCaseImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private GetProductUseCaseImpl getProductUseCase;

    @BeforeEach
     void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
     void testGetProductFound() {
        GetProductRequest request = new GetProductRequest();
        request.setId(1L);

        ProductEntity productEntity = new ProductEntity();
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(productEntity));

        GetProductResponse response = getProductUseCase.getProduct(request);

        assertNotNull(response);
        assertEquals(productEntity, response.getProductEntity());
        verify(productRepository, times(1)).findById(anyLong());
    }

    @Test
     void testGetProductNotFound() {
        GetProductRequest request = new GetProductRequest();
        request.setId(1L);

        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        GetProductResponse response = getProductUseCase.getProduct(request);

        assertNull(response);
        verify(productRepository, times(1)).findById(anyLong());
    }
}
