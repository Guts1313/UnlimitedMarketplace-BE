package unlimitedmarketplace.business.impl;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unlimitedmarketplace.business.BidService;
import unlimitedmarketplace.domain.BidRequest;
import unlimitedmarketplace.domain.GetMyBiddedProductsRequest;
import unlimitedmarketplace.domain.GetMyBiddedProductsResponse;
import unlimitedmarketplace.domain.ProductStatus;
import unlimitedmarketplace.persistence.BidRepository;
import unlimitedmarketplace.persistence.ProductRepository;
import unlimitedmarketplace.persistence.UserRepository;
import unlimitedmarketplace.persistence.entity.BidEntity;
import unlimitedmarketplace.persistence.entity.ProductEntity;
import unlimitedmarketplace.persistence.entity.UserEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

@Service
public class BidServiceImpl implements BidService {
    private final BidRepository bidRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final Logger logs = LoggerFactory.getLogger(BidServiceImpl.class);

    public BidServiceImpl(BidRepository bidRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.bidRepository = bidRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public GetMyBiddedProductsResponse findBiddedProductsById(GetMyBiddedProductsRequest request) {

//        List<ProductEntity> productEntities = bidRepository.findProductsByUserId(request.getUserId());
//        for (ProductEntity prod: productEntities){
//            Long prodId = prod.getId();
//            BigDecimal latestPrice = findLatestBidAmountByProductId(prodId);
//            prod.setProductPrice(latestPrice.doubleValue());
//        }
//        return GetMyBiddedProductsResponse.builder()
//                .userBidProducts(productEntities)
//                .userId(request.getUserId())
//                .build();
        Long id = request.getUserId();
        List<BidEntity> userBidProducts = bidRepository.findBiddedProductsByUserId(id);
        GetMyBiddedProductsResponse response = new GetMyBiddedProductsResponse();
        response.setUserId(id);
        response.setUserBidProducts(userBidProducts);
        return response;
    }

    @Transactional
    public BidEntity acceptBid(Long userId, BigDecimal bidAmount) {
        logs.info("User id in bidserviceimpl: {}" , userId);
        logs.info("Bid amount in bidserviceimpl: {}" , bidAmount);

        BidEntity bid = bidRepository.findFirstByAmountAndUserIdOrderByBidTimeDesc(bidAmount,userId);
        if (bid != null) {
            bid.setBidStatus("ACCEPTED");
            bidRepository.save(bid);
            ProductEntity product = bid.getProduct();
            product.setProductStatus(String.valueOf(ProductStatus.SOLD));
            productRepository.saveAndFlush(product);
        } else {
            logs.error("Bid not found with amount: {} and userId: {}", bidAmount, userId);
            throw new EntityNotFoundException("Bid not found");
        }
        return bid;
    }

    public BigDecimal findLatestBidAmountByProductId(Long productId) {
        Pageable pageable = PageRequest.of(0, 1); // Get only the first result
        Page<BidEntity> page = bidRepository.findHighestBidByProductId(productId, pageable);

        if (page.hasContent()) {
            return page.getContent().get(0).getAmount(); // Return the amount of the first (and only) bid
        } else {
            return BigDecimal.ZERO; // Return ZERO if no bids are present
        }
    }

    public List<ProductEntity> findProductsByUserId(Long userId) {
        return bidRepository.findProductsByUserId(userId);
    }

    public List<String> getAllBiddersExceptLatest(Long productId, String latestBidderUsername) {
        return bidRepository.findAllBiddersExceptLatest(productId, latestBidderUsername);
    }

    @Override
    public BidEntity placeBid(BidRequest bidRequest) {
        ProductEntity product = productRepository.findById(bidRequest.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        UserEntity user = userRepository.findById(bidRequest.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Pageable limitOne = PageRequest.of(0, 1);
        Page<BidEntity> highestBidPage = bidRepository.findHighestBidByProductId(product.getId(), limitOne);
        Optional<BidEntity> highestBidOptional = highestBidPage.get().findFirst(); // Fetch the first and only result

        if (highestBidOptional.isPresent()) {
            BigDecimal highestBidAmount = highestBidOptional.get().getAmount();
            if (bidRequest.getBidAmount().compareTo(highestBidAmount) <= 0) {
                throw new IllegalArgumentException("New bid must be higher than the current highest bid.");
            }
        }
        // Allow the first bid on the product if no previous bids exist
        BidEntity newBid = new BidEntity();
        newBid.setAmount(bidRequest.getBidAmount());
        newBid.setBidTime(LocalDateTime.now());
        newBid.setProduct(product);
        newBid.setUser(user);

        return bidRepository.save(newBid);
    }


}
