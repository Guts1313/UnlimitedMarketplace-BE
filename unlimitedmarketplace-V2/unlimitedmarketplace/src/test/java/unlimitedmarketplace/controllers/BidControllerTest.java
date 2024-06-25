package unlimitedmarketplace.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import unlimitedmarketplace.business.interfaces.BidService;
import unlimitedmarketplace.business.impl.SubscriptionService;
import unlimitedmarketplace.domain.*;

import unlimitedmarketplace.persistence.entity.BidEntity;
import unlimitedmarketplace.persistence.entity.ProductEntity;
import unlimitedmarketplace.persistence.entity.UserEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class BidControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private SimpMessageHeaderAccessor headerAccessor;

    @Mock
    private BidService bidService;

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private BidController bidController;


    @Test
    void testGetLatestBidSuccess() {
        Long productId = 1L;
        BigDecimal bidAmount = new BigDecimal("100.00");
        when(bidService.findLatestBidAmountByProductId(productId)).thenReturn(bidAmount);

        ResponseEntity<?> response = bidController.getLatestBid(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(BidResponse.class, response.getBody());
        assertEquals(bidAmount, ((BidResponse) response.getBody()).getBidAmount());
    }


    @Test
    void testGetLatestBidFailure() {
        Long productId = 1L;
        when(bidService.findLatestBidAmountByProductId(productId)).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = bidController.getLatestBid(productId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(null, response.getBody());
    }



    @Test
    void testHandleBidFailure() {
        BidRequest bidRequest = new BidRequest(1L, 2L, new BigDecimal("200.00"));
        when(bidService.placeBid(bidRequest)).thenThrow(new RuntimeException("Database error"));
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user1");

        headerAccessor = mock(SimpMessageHeaderAccessor.class);
        when(headerAccessor.getUser()).thenReturn(principal);
        bidController.handleBid(bidRequest, headerAccessor);

        verify(messagingTemplate, times(1)).convertAndSendToUser(eq("user1"), eq("/queue/bidResponse"), any(BidResponse.class));
    }


    @Test
    void testHandleBidWithUnauthenticatedUser() {
        BidRequest bidRequest = new BidRequest(1L, 2L, new BigDecimal("200.00"));

        SimpMessageHeaderAccessor headerAccessor = mock(SimpMessageHeaderAccessor.class);
        when(headerAccessor.getUser()).thenReturn(null);

        when(bidService.placeBid(bidRequest)).thenReturn(new BidEntity());

        bidController.handleBid(bidRequest, headerAccessor);

        verify(messagingTemplate, never()).convertAndSendToUser(anyString(), anyString(), any());
    }


    @Test
    void testHandleMaxIntegerBidAmount() {
        BidRequest bidRequest = new BidRequest(1L, 2L, new BigDecimal(Integer.MAX_VALUE));
        BidEntity bidEntity = new BidEntity();
        bidEntity.setAmount(new BigDecimal(Integer.MAX_VALUE));

        ProductEntity product = new ProductEntity();
        product.setId(1L);
        bidEntity.setProduct(product);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(2L);
        userEntity.setUserName("user1");
        bidEntity.setUser(userEntity);

        when(bidService.placeBid(bidRequest)).thenReturn(bidEntity);

        bidController.handleBid(bidRequest, mock(SimpMessageHeaderAccessor.class));

        verify(messagingTemplate).convertAndSend(eq("/topic/product1"), any(BidResponse.class));
    }

    @Test
    void testHandleBidFailureDueToNullBid() {
        SimpMessageHeaderAccessor headerAccessor = mock(SimpMessageHeaderAccessor.class);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bidController.handleBid(null, headerAccessor);
        });

        assertEquals("Bid request cannot be null.", exception.getMessage());

        verify(messagingTemplate, never()).convertAndSend(anyString(), any(BidResponse.class));
    }
    @Test
    void testAcceptBidSuccess() {
        AcceptBidRequest acceptBidRequest = new AcceptBidRequest("200.00", 2L);

        ProductEntity product = new ProductEntity();
        product.setId(1L);
        BidEntity bidEntity = new BidEntity();
        bidEntity.setAmount(new BigDecimal("200.00"));
        bidEntity.setProduct(product);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(2L);
        userEntity.setUserName("user1");
        bidEntity.setUser(userEntity);

        when(bidService.acceptBid(acceptBidRequest.getUserId(), new BigDecimal(acceptBidRequest.getBidAmount()).setScale(2, RoundingMode.HALF_UP))).thenReturn(bidEntity);

        bidController.acceptBid(acceptBidRequest);

        verify(messagingTemplate, times(1)).convertAndSendToUser(eq("user1"), eq("/queue/winner1"), anyString());
    }

    @Test
    void testAcceptBidFailure() {
        AcceptBidRequest acceptBidRequest = new AcceptBidRequest("200.00", 2L);

        when(bidService.acceptBid(anyLong(), any(BigDecimal.class))).thenThrow(new RuntimeException("Database error"));

        bidController.acceptBid(acceptBidRequest);

        verify(messagingTemplate, never()).convertAndSendToUser(anyString(), anyString(), anyString());
    }

    @Test
    void testGetUserBidsSuccess() {
        Long userId = 1L;
        GetMyBiddedProductsResponse response = new GetMyBiddedProductsResponse();
        when(bidService.findBiddedProductsById(any(GetMyBiddedProductsRequest.class))).thenReturn(response);

        ResponseEntity<GetMyBiddedProductsResponse> result = bidController.getUserBids(userId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void testGetUserBidsFailure() {
        Long userId = 1L;
        when(bidService.findBiddedProductsById(any(GetMyBiddedProductsRequest.class))).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<GetMyBiddedProductsResponse> result = bidController.getUserBids(userId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertNull(result.getBody());
    }


}