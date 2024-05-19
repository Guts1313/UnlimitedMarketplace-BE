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
import unlimitedmarketplace.business.BidService;
import unlimitedmarketplace.domain.BidRequest;
import unlimitedmarketplace.domain.BidResponse;

import unlimitedmarketplace.persistence.entity.BidEntity;
import unlimitedmarketplace.persistence.entity.ProductEntity;
import unlimitedmarketplace.persistence.entity.UserEntity;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class BidControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;
    private SimpMessageHeaderAccessor headerAccessor;
    @Mock
    private BidService bidService;
    @InjectMocks
    private BidController bidController;

//    @BeforeEach
//    void setUp() {
//        Principal principal = mock(Principal.class);
//        when(principal.getName()).thenReturn("user1");
//
//        headerAccessor = mock(SimpMessageHeaderAccessor.class);
//        when(headerAccessor.getUser()).thenReturn(principal);
//    }


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
    void testHandleBidSuccess() {
        // Setup request and entity objects
        BidRequest bidRequest = new BidRequest(1L, 2L, new BigDecimal("200.00"));
        BidEntity bidEntity = new BidEntity();
        bidEntity.setAmount(new BigDecimal("200.00"));
        bidEntity.setId(2L);

        // Set up ProductEntity
        ProductEntity product = new ProductEntity();
        product.setId(1L);
        bidEntity.setProduct(product);

        // Set up UserEntity and associate it with BidEntity
        UserEntity userEntity = new UserEntity();
        userEntity.setId(2L);
        userEntity.setUserName("user1");
        bidEntity.setUser(userEntity);
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user1");

        headerAccessor = mock(SimpMessageHeaderAccessor.class);
        when(headerAccessor.getUser()).thenReturn(principal);
        // Mock the service to return the prepared entity
        when(bidService.placeBid(bidRequest)).thenReturn(bidEntity);
        when(bidService.getAllBiddersExceptLatest(anyLong(), anyString())).thenReturn(Collections.singletonList("user2"));

        // Mock messaging template to do nothing when methods are called
        doNothing().when(messagingTemplate).convertAndSend(anyString(), any(BidResponse.class));
        doNothing().when(messagingTemplate).convertAndSendToUser(anyString(), anyString(), anyString());

        // Call the method under test
        bidController.handleBid(bidRequest, headerAccessor);

        // Verify interactions
        verify(messagingTemplate).convertAndSend(eq("/topic/product1"), any(BidResponse.class));
        verify(messagingTemplate).convertAndSendToUser(eq("user2"), eq("/queue/outbid1"), anyString());
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
        // Create the BidRequest
        BidRequest bidRequest = new BidRequest(1L, 2L, new BigDecimal("200.00"));

        // Mock SimpMessageHeaderAccessor to return null for getUser
        SimpMessageHeaderAccessor headerAccessor = mock(SimpMessageHeaderAccessor.class);
        when(headerAccessor.getUser()).thenReturn(null);

        // Ensure a valid bid is returned from the bidService
        when(bidService.placeBid(bidRequest)).thenReturn(new BidEntity());

        // Call the handleBid method
        bidController.handleBid(bidRequest, headerAccessor);

        // Verify that messagingTemplate.convertAndSendToUser is never called
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
        // Mock SimpMessageHeaderAccessor
        SimpMessageHeaderAccessor headerAccessor = mock(SimpMessageHeaderAccessor.class);

        // Verify that the exception was thrown
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bidController.handleBid(null, headerAccessor);
        });

        assertEquals("Bid request cannot be null.", exception.getMessage());

        // Verify that messagingTemplate.convertAndSend is never called
        verify(messagingTemplate, never()).convertAndSend(anyString(), any(BidResponse.class));
    }


//    @Test
//    void testPlaceBidSuccessfullyHigherThanCurrent() {
//        // Arrange
//        Long productId = 1L;
//        Long userId = 1L;
//        BigDecimal newBidAmount = new BigDecimal("200.00");
//        BigDecimal existingBidAmount = new BigDecimal("150.00");
//
//        ProductEntity product = new ProductEntity();
//        product.setId(productId);
//        UserEntity user = new UserEntity();
//        user.setId(userId);
//
//        BidEntity highestBid = new BidEntity();
//        highestBid.setAmount(existingBidAmount);
//
//        // Mock the repository to return our product and user
//        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        // Return a page containing the highest bid
//        when(bidRepository.findHighestBidByProductId(eq(productId), any(Pageable.class)))
//                .thenReturn(new PageImpl<>(Collections.singletonList(highestBid)));
//
//        BidEntity savedBid = new BidEntity();
//        savedBid.setAmount(newBidAmount);
//        when(bidRepository.save(any(BidEntity.class))).thenReturn(savedBid);
//
//        BidRequest bidRequest = new BidRequest(productId, userId, newBidAmount);
//
//        // Act
//        BidEntity result = bidService.placeBid(bidRequest);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(newBidAmount, result.getAmount());
//        verify(bidRepository).save(any(BidEntity.class));
//    }

//    @Test
//    void testPlaceBidSuccessfullyWhenNoPreviousBids() {
//        // Arrange
//        Long productId = 1L;
//        Long userId = 2L;
//        BigDecimal bidAmount = new BigDecimal("200");
//
//        ProductEntity product = new ProductEntity();
//        product.setId(productId);
//        UserEntity user = new UserEntity();
//        user.setId(userId);
//
//        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(bidRepository.findHighestBidByProductId(productId, Pageable.unpaged())).thenReturn(Page.empty());
//
//        BidEntity savedBid = new BidEntity();
//        savedBid.setAmount(bidAmount);
//        when(bidRepository.save(any(BidEntity.class))).thenReturn(savedBid);
//
//        BidRequest bidRequest = new BidRequest(productId, userId, bidAmount);
//
//        // Act
//        BidEntity result = bidService.placeBid(bidRequest);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(bidAmount, result.getAmount());
//        verify(bidRepository).save(any(BidEntity.class));
//    }

//    @Test
//    void testPlaceBidFailsWhenNotHigherThanCurrent() {
//        // Arrange
//        Long productId = 1L;
//        Long userId = 2L;
//        BigDecimal newBidAmount = new BigDecimal("200.00");
//        BigDecimal highestBidAmount = new BigDecimal("300.00");
//
//        ProductEntity product = new ProductEntity();
//        product.setId(productId);
//        UserEntity user = new UserEntity();
//        user.setId(userId);
//
//        BidEntity highestBid = new BidEntity();
//        highestBid.setAmount(highestBidAmount);
//
//        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(bidRepository.findHighestBidByProductId(productId, Pageable.unpaged()))
//                .thenReturn(new PageImpl<>(Collections.singletonList(highestBid)));
//
//        BidRequest bidRequest = new BidRequest(productId, userId, newBidAmount);
//        SimpMessageHeaderAccessor headerAccessor = mock(SimpMessageHeaderAccessor.class);
//
//        // Act and Assert
//        IllegalArgumentException exception = new IllegalArgumentException("New bid must be higher than the current highest bid.");
//        // Assert the exception message
//        assertEquals("New bid must be higher than the current highest bid.", exception.getMessage());
//
//        verify(bidRepository, never()).save(any(BidEntity.class));
//    }


}