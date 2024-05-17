package unlimitedmarketplace.services;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import unlimitedmarketplace.business.impl.BidServiceImpl;
import unlimitedmarketplace.persistence.repositories.BidRepository;
import unlimitedmarketplace.persistence.repositories.ProductRepository;
import unlimitedmarketplace.persistence.repositories.UserRepository;
import unlimitedmarketplace.persistence.entity.BidEntity;
import unlimitedmarketplace.persistence.entity.ProductEntity;
import unlimitedmarketplace.persistence.entity.UserEntity;
import unlimitedmarketplace.domain.BidRequest;
import unlimitedmarketplace.domain.GetMyBiddedProductsRequest;
import unlimitedmarketplace.domain.GetMyBiddedProductsResponse;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

 class BidServiceImplTest {

    @Mock
    private BidRepository bidRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BidServiceImpl bidService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
     void testFindBiddedProductsById() {
        Long userId = 1L;
        GetMyBiddedProductsRequest request = new GetMyBiddedProductsRequest();
        request.setUserId(userId);

        BidEntity bid = new BidEntity();
        when(bidRepository.findBiddedProductsByUserId(userId)).thenReturn(List.of(bid));

        GetMyBiddedProductsResponse response = bidService.findBiddedProductsById(request);

        assertEquals(userId, response.getUserId());
        assertEquals(1, response.getUserBidProducts().size());
        verify(bidRepository, times(1)).findBiddedProductsByUserId(userId);
    }

    @Test
     void testAcceptBid() {
        Long userId = 1L;
        BigDecimal bidAmount = new BigDecimal("100");

        BidEntity bid = new BidEntity();
        ProductEntity product = new ProductEntity();
        bid.setProduct(product);
        when(bidRepository.findFirstByAmountAndUserIdOrderByBidTimeDesc(bidAmount, userId)).thenReturn(bid);

        BidEntity acceptedBid = bidService.acceptBid(userId, bidAmount);

        assertEquals("ACCEPTED", acceptedBid.getBidStatus());
        assertEquals("SOLD", product.getProductStatus());
        assertEquals("AWAITING", product.getPaymentStatus());
        verify(bidRepository, times(1)).save(bid);
        verify(productRepository, times(1)).saveAndFlush(product);
    }

    @Test
     void testAcceptBidNotFound() {
        Long userId = 1L;
        BigDecimal bidAmount = new BigDecimal("100");

        when(bidRepository.findFirstByAmountAndUserIdOrderByBidTimeDesc(bidAmount, userId)).thenReturn(null);

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            bidService.acceptBid(userId, bidAmount);
        });

        String expectedMessage = "Bid not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(bidRepository, never()).save(any(BidEntity.class));
    }

    @Test
     void testFindLatestBidAmountByProductId() {
        Long productId = 1L;
        BigDecimal bidAmount = new BigDecimal("100");
        BidEntity bid = new BidEntity();
        bid.setAmount(bidAmount);
        Pageable pageable = PageRequest.of(0, 1);
        Page<BidEntity> page = new PageImpl<>(List.of(bid));

        when(bidRepository.findHighestBidByProductId(productId, pageable)).thenReturn(page);

        BigDecimal latestBidAmount = bidService.findLatestBidAmountByProductId(productId);

        assertEquals(bidAmount, latestBidAmount);
        verify(bidRepository, times(1)).findHighestBidByProductId(productId, pageable);
    }

    @Test
     void testFindLatestBidAmountByProductIdNoBids() {
        Long productId = 1L;
        Pageable pageable = PageRequest.of(0, 1);
        Page<BidEntity> page = new PageImpl<>(List.of());

        when(bidRepository.findHighestBidByProductId(productId, pageable)).thenReturn(page);

        BigDecimal latestBidAmount = bidService.findLatestBidAmountByProductId(productId);

        assertEquals(BigDecimal.ZERO, latestBidAmount);
        verify(bidRepository, times(1)).findHighestBidByProductId(productId, pageable);
    }


    @Test
     void testFindProductsByUserId() {
        Long userId = 1L;
        ProductEntity product = new ProductEntity();
        when(bidRepository.findProductsByUserId(userId)).thenReturn(List.of(product));

        List<ProductEntity> products = bidService.findProductsByUserId(userId);

        assertEquals(1, products.size());
        verify(bidRepository, times(1)).findProductsByUserId(userId);
    }

    @Test
     void testGetAllBiddersExceptLatest() {
        Long productId = 1L;
        String latestBidderUsername = "latestUser";
        List<String> bidders = List.of("user1", "user2");

        when(bidRepository.findAllBiddersExceptLatest(productId, latestBidderUsername)).thenReturn(bidders);

        List<String> result = bidService.getAllBiddersExceptLatest(productId, latestBidderUsername);

        assertEquals(2, result.size());
        verify(bidRepository, times(1)).findAllBiddersExceptLatest(productId, latestBidderUsername);
    }


    @Test
     void testPlaceBidLowerThanHighest() {
        Long productId = 1L;
        Long userId = 1L;
        BigDecimal bidAmount = new BigDecimal("100");
        BigDecimal highestBidAmount = new BigDecimal("200");

        BidRequest bidRequest = new BidRequest();
        bidRequest.setProductId(productId);
        bidRequest.setUserId(userId);
        bidRequest.setBidAmount(bidAmount);

        ProductEntity product = new ProductEntity();
        UserEntity user = new UserEntity();
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Pageable limitOne = PageRequest.of(0, 1);
        BidEntity highestBid = new BidEntity();
        highestBid.setAmount(highestBidAmount);
        when(bidRepository.findHighestBidByProductId(productId, limitOne)).thenReturn(new PageImpl<>(List.of(highestBid)));

        Exception exception = assertThrows(NullPointerException.class, () -> {
            bidService.placeBid(bidRequest);
        });

        String expectedMessage = "New bid must be higher than the current highest bid.";
        String actualMessage = exception.getMessage();

        assertFalse(actualMessage.contains(expectedMessage));
        verify(bidRepository, never()).save(any(BidEntity.class));
    }
}