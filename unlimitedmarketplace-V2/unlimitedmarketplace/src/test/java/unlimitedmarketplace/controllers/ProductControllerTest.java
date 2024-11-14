package unlimitedmarketplace.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import unlimitedmarketplace.business.interfaces.CreateProductUseCase;
import unlimitedmarketplace.business.interfaces.GetAllProductsUseCase;
import unlimitedmarketplace.business.interfaces.GetProductUseCase;
import unlimitedmarketplace.configuration.AuthenticationRequestFilter;
import unlimitedmarketplace.domain.CreateProductRequest;
import unlimitedmarketplace.domain.CreateProductResponse;
import unlimitedmarketplace.domain.GetAllProductsRequest;
import unlimitedmarketplace.domain.GetAllProductsResponse;
import unlimitedmarketplace.domain.GetProductRequest;
import unlimitedmarketplace.domain.GetProductResponse;
import unlimitedmarketplace.security.AccessTokenDecoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import({AuthenticationRequestFilter.class})

 class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateProductUseCase createProductUseCase;

    @MockBean
    private GetAllProductsUseCase getAllProducts;
    @MockBean
    private AccessTokenDecoder accessTokenDecoder;

    @MockBean
    private GetProductUseCase getProductUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "USER")
     void testCreateProduct() throws Exception {
        CreateProductRequest request = new CreateProductRequest();
        CreateProductResponse response = new CreateProductResponse(1L,"good","asd.com",Double.valueOf(123),"24/7/25");
        when(createProductUseCase.createProduct(any(CreateProductRequest.class))).thenReturn(response);

        mockMvc.perform(post("/unlimitedmarketplace/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
     void testGetAllProducts() throws Exception {
        GetAllProductsResponse response = new GetAllProductsResponse();
        when(getAllProducts.getAllProducts(any(GetAllProductsRequest.class))).thenReturn(response);

        mockMvc.perform(get("/unlimitedmarketplace/products")
                        .param("productCat", "testCategory"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(getAllProducts, times(1)).getAllProducts(any(GetAllProductsRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
     void testGetUserListedProducts() throws Exception {
        GetAllProductsResponse response = new GetAllProductsResponse();
        when(getAllProducts.getAllListedProductsByUserId(any(GetAllProductsRequest.class))).thenReturn(response);

        mockMvc.perform(get("/unlimitedmarketplace/products/mylistings")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(getAllProducts, times(1)).getAllListedProductsByUserId(any(GetAllProductsRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
     void testGetProduct() throws Exception {
        GetProductResponse response = new GetProductResponse();
        when(getProductUseCase.getProduct(any(GetProductRequest.class))).thenReturn(response);

        mockMvc.perform(get("/unlimitedmarketplace/products/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(getProductUseCase, times(1)).getProduct(any(GetProductRequest.class));
    }
}
