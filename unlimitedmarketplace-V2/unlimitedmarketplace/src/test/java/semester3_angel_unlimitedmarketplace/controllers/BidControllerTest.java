package semester3_angel_unlimitedmarketplace.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import semester3_angel_unlimitedmarketplace.business.BidService;
import semester3_angel_unlimitedmarketplace.domain.BidRequest;
import semester3_angel_unlimitedmarketplace.domain.BidResponse;
import semester3_angel_unlimitedmarketplace.persistence.BidRepository;
import semester3_angel_unlimitedmarketplace.persistence.ProductRepository;
import semester3_angel_unlimitedmarketplace.persistence.UserRepository;
import semester3_angel_unlimitedmarketplace.persistence.entity.BidEntity;
import semester3_angel_unlimitedmarketplace.persistence.entity.ProductEntity;
import semester3_angel_unlimitedmarketplace.persistence.entity.UserEntity;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BidControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private BidService bidService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BidRepository bidRepository;
    @Mock
    private ProductRepository productRepository;
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
        assertTrue(response.getBody() instanceof BidResponse);
        assertEquals(bidAmount, ((BidResponse) response.getBody()).getBidAmount());
    }

    @Test
    void testGetLatestBidFailure() {
        Long productId = 1L;
        when(bidService.findLatestBidAmountByProductId(productId)).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = bidController.getLatestBid(productId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to fetch latest bid", response.getBody());
    }

    @Test
    void testHandleBidSuccess() {
        // Setup request and entity objects
        BidRequest bidRequest = new BidRequest(1L, 2L, new BigDecimal("200.00"));
        BidEntity bidEntity = new BidEntity();
        bidEntity.setAmount(new BigDecimal("200.00"));
        ProductEntity product = new ProductEntity();
        product.setId(1L);
        bidEntity.setProduct(product);

        // Mock the service to return the prepared entity
        when(bidService.placeBid(bidRequest)).thenReturn(bidEntity);

        // Mock messaging template to do nothing when methods are called
        doNothing().when(messagingTemplate).convertAndSend(anyString(), any(BidResponse.class));
        doNothing().when(messagingTemplate).convertAndSendToUser(anyString(), anyString(), anyString());

        // Prepare and mock the header accessor
        SimpMessageHeaderAccessor headerAccessor = mock(SimpMessageHeaderAccessor.class);
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("user1");
        when(headerAccessor.getUser()).thenReturn(mockPrincipal);

        // Call the method under test
        bidController.handleBid(bidRequest, headerAccessor);

        // Verify interactions
        verify(messagingTemplate).convertAndSend(eq("/topic/product1"), any(BidResponse.class));
        verify(messagingTemplate).convertAndSendToUser(eq("user1"), eq("/queue/outbid1"), anyString());
    }


    @Test
    void testHandleBidFailure() {
        BidRequest bidRequest = new BidRequest(1L, 2L, new BigDecimal("200.00"));
        when(bidService.placeBid(bidRequest)).thenThrow(new RuntimeException("Database error"));
        SimpMessageHeaderAccessor headerAccessor = mock(SimpMessageHeaderAccessor.class);
        when(headerAccessor.getUser()).thenReturn(() -> "user1");

        bidController.handleBid(bidRequest, headerAccessor);

        verify(messagingTemplate, times(1)).convertAndSendToUser(eq("user1"), eq("/queue/bidResponse"), any(BidResponse.class));
    }


    @Test
    void testHandleBidWithUnauthenticatedUser() {
        BidRequest bidRequest = new BidRequest(1L, 2L, new BigDecimal("200.00"));
        SimpMessageHeaderAccessor headerAccessor = mock(SimpMessageHeaderAccessor.class);
        when(headerAccessor.getUser()).thenReturn(null);  // Unauthenticated user

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

        when(bidService.placeBid(bidRequest)).thenReturn(bidEntity);

        bidController.handleBid(bidRequest, mock(SimpMessageHeaderAccessor.class));

        verify(messagingTemplate).convertAndSend(eq("/topic/product1"), any(BidResponse.class));
    }


    @Test
    void testHandleBidFailureDueToNullBid() {
        // Arrange: Simulate bid creation failure
        when(bidService.placeBid(any())).thenReturn(null);
        SimpMessageHeaderAccessor headerAccessor = mock(SimpMessageHeaderAccessor.class);

        // Act and Assert: Expect IllegalStateException due to null bid
        BidRequest bidRequest = new BidRequest(1L, 2L, new BigDecimal("200"));
        IllegalStateException exception = new IllegalStateException();
        bidController.handleBid(bidRequest, headerAccessor);
        assertEquals(new IllegalStateException().getMessage(), exception.getMessage());
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