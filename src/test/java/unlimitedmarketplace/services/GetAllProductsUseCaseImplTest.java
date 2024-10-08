package unlimitedmarketplace.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import unlimitedmarketplace.business.impl.GetAllProductsUseCaseImpl;
import unlimitedmarketplace.domain.GetAllProductsRequest;
import unlimitedmarketplace.domain.GetAllProductsResponse;
import unlimitedmarketplace.persistence.repositories.ProductRepository;
import unlimitedmarketplace.persistence.entity.ProductEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

 class GetAllProductsUseCaseImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private GetAllProductsUseCaseImpl getAllProductsUseCase;

    @BeforeEach
     void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
     void testGetAllProductsByCategory() {
        GetAllProductsRequest request = new GetAllProductsRequest();
        request.setProductsCat("Electronics");

        List<ProductEntity> products = List.of(new ProductEntity(), new ProductEntity());
        when(productRepository.findProductEntitiesByProductNameLike(anyString())).thenReturn(products);

        GetAllProductsResponse response = getAllProductsUseCase.getAllProducts(request);

        assertNotNull(response);
        assertEquals(2, response.getProductEntities().size());
        verify(productRepository, times(1)).findProductEntitiesByProductNameLike(anyString());
    }

    @Test
     void testGetAllActiveProducts() {
        GetAllProductsRequest request = new GetAllProductsRequest();

        List<ProductEntity> products = List.of(new ProductEntity(), new ProductEntity());
        when(productRepository.findAllByProductStatus(anyString())).thenReturn(products);

        GetAllProductsResponse response = getAllProductsUseCase.getAllProducts(request);

        assertNotNull(response);
        assertEquals(2, response.getProductEntities().size());
        verify(productRepository, times(1)).findAllByProductStatus(anyString());
    }

    @Test
     void testGetAllListedProductsByUserId() {
        GetAllProductsRequest request = new GetAllProductsRequest();
        request.setId(1L);

        List<ProductEntity> products = List.of(new ProductEntity(), new ProductEntity());
        when(productRepository.findListedByUserId(anyLong())).thenReturn(products);

        GetAllProductsResponse response = getAllProductsUseCase.getAllListedProductsByUserId(request);

        assertNotNull(response);
        assertEquals(2, response.getProductEntities().size());
        verify(productRepository, times(1)).findListedByUserId(anyLong());
    }
}
