package unlimitedmarketplace.domain;

import org.junit.jupiter.api.Test;
import org.springframework.data.auditing.CurrentDateTimeProvider;
import unlimitedmarketplace.persistence.entity.ProductEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DomainEntitiesTest {

    @Test
    void testAcceptBidRequest() {
        AcceptBidRequest request = new AcceptBidRequest("200.00", 1L);
        assertEquals("200.00", request.getBidAmount());
        assertEquals(1L, request.getUserId());
    }

    @Test
    void testAuthResponse() {
        AuthResponse response = new AuthResponse("token123", "test1234");
        assertEquals("token123", response.getAccessToken());
        assertEquals("test1234", response.getRefreshToken());

    }

    @Test
    void testBidRequest() {
        BidRequest request = new BidRequest(1L, 2L, new BigDecimal("200.00"));
        assertEquals(1L, request.getProductId());
        assertEquals(2L, request.getUserId());
        assertEquals(new BigDecimal("200.00"), request.getBidAmount());
    }

    @Test
    void testBidResponse() {
        BidResponse response = new BidResponse(1L, 2L, new BigDecimal("200.00"), "success");
        assertEquals(1L, response.getProductId());
        assertEquals(2L, response.getUserId());
        assertEquals(new BigDecimal("200.00"), response.getBidAmount());
        assertNull(response.getStatus());
    }

    @Test
    void testCreateProductRequest() {
        CreateProductRequest request = new CreateProductRequest(1L, "Sneakers", "test@test", 100.00, CurrentDateTimeProvider.INSTANCE.getNow().toString());
        assertEquals("Sneakers", request.getProductName());
        assertEquals(100.00, request.getProductPrice());
        assertEquals("test@test", request.getProductUrl());
    }

    @Test
    void testCreateProductResponse() {
        CreateProductResponse response = new CreateProductResponse(1L, "Sneakers", "www.test/", 100, CurrentDateTimeProvider.INSTANCE.getNow().toString());
        assertEquals(1L, response.getId());
        assertEquals("Sneakers", response.getProductName());
        assertEquals(100.00, response.getProductPrice());
    }

    @Test
    void testCreateUserRequest() {
        CreateUserRequest request = new CreateUserRequest("john", "password@email", "john@example.com", UserRoles.USER);
        assertEquals("john", request.getUserName());
        assertEquals("john@example.com", request.getPasswordHash());
        assertEquals("password@email", request.getEmail());
        assertEquals(UserRoles.USER, request.getRole());

    }

    @Test
    void testCreateUserResponse() {
        CreateUserResponse response = new CreateUserResponse(1L, "john", "test@test", UserRoles.USER);
        assertEquals(1L, response.getId());
        assertEquals("john", response.getUserName());
        assertEquals("test@test", response.getEmail());
        assertEquals(UserRoles.USER, response.getRole());

    }

    @Test
    void testDeleteUserRequest() {
        DeleteUserRequest request = new DeleteUserRequest(1L);
        assertEquals(1L, request.getId());
    }

    @Test
    void testGetAllProductsRequest() {
        GetAllProductsRequest request = new GetAllProductsRequest();
    }

    @Test
    void testGetAllProductsResponse() {
        GetAllProductsResponse response = new GetAllProductsResponse();
    }

    @Test
    void testGetAllUsersRequest() {
        GetAllUsersRequest request = new GetAllUsersRequest();
    }

    @Test
    void testGetAllUsersResponse() {
        GetAllUsersResponse response = new GetAllUsersResponse();
    }

    @Test
    void testGetMyBiddedProductsRequest() {
        GetMyBiddedProductsRequest request = new GetMyBiddedProductsRequest(1L);
        assertEquals(1L, request.getUserId());
    }

    @Test
    void testGetMyBiddedProductsResponse() {
        GetMyBiddedProductsResponse response = new GetMyBiddedProductsResponse();
    }

    @Test
    void testGetProductRequest() {
        GetProductRequest request = new GetProductRequest(1L);
        assertEquals(1L, request.getId());
    }

    @Test
    void testGetProductResponse() {
        ProductEntity entity = new ProductEntity();
        entity.setId(1L);
        entity.setProductStatus(ProductStatus.ACTIVE.toString());
        entity.setProductUrl("https:3000");
        entity.setProductPrice(100.00);
        GetProductResponse response = new GetProductResponse(entity);
        assertEquals(1L, response.getProductEntity().getId());
        assertEquals(ProductStatus.ACTIVE.toString(), response.getProductEntity().getProductStatus());
        assertEquals(100.00, response.getProductEntity().getProductPrice());
        assertEquals("https:3000", response.getProductEntity().getProductUrl());
    }

    @Test
    void testGetUserRequest() {
        GetUserRequest request = new GetUserRequest();
        request.setId(1L);
        assertEquals(1L, request.getId());
    }

    @Test
    void testGetUserResponse() {
        GetUserResponse response = new GetUserResponse(1L, "john", "john@example.com", UserRoles.USER.toString());
        assertEquals(1L, response.getId());
        assertEquals("john", response.getUsername());
        assertEquals("john@example.com", response.getEmail());
        assertEquals(UserRoles.USER.toString(), response.getRole());

    }

    @Test
    void testLoginRequest() {
        LoginRequest request = new LoginRequest("john", "password");
        assertEquals("john", request.getUsername());
        assertEquals("password", request.getPasswordHash());
    }

    @Test
    void testLoginResponse() {
        LoginResponse response = new LoginResponse("token123", "token1234", 1L);
        assertEquals("token123", response.getAccessToken());
    }

    @Test
    void testProductStatus() {
        ProductStatus status = ProductStatus.ACTIVE;
        assertEquals("ACTIVE", status.name());
    }

    @Test
    void testUpdateUserPasswordRequest() {
        UpdateUserPasswordRequest request = new UpdateUserPasswordRequest(1L, "newpassword");
        assertEquals(1L, request.getId());
        assertEquals("newpassword", request.getNewPassword());
    }

    @Test
    void testUser() {
        User user = new User(1L, "john", "password", "john@example.com", UserRoles.USER);
        assertEquals(1L, user.getId());
        assertEquals("john", user.getUserName());
        assertEquals("password", user.getPasswordHash());
        assertEquals("john@example.com", user.getEmail());
        assertEquals(UserRoles.USER, user.getRole());

    }

    @Test
    void testUserRoles() {
        UserRoles role = UserRoles.USER;
        assertEquals("USER", role.name());
    }
}
