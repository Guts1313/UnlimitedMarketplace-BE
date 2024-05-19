package semester3_angel_unlimitedmarketplace.business.impl;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import semester3_angel_unlimitedmarketplace.business.BidService;
import semester3_angel_unlimitedmarketplace.domain.BidRequest;
import semester3_angel_unlimitedmarketplace.persistence.BidRepository;
import semester3_angel_unlimitedmarketplace.persistence.ProductRepository;
import semester3_angel_unlimitedmarketplace.persistence.UserRepository;
import semester3_angel_unlimitedmarketplace.persistence.entity.BidEntity;
import semester3_angel_unlimitedmarketplace.persistence.entity.ProductEntity;
import semester3_angel_unlimitedmarketplace.persistence.entity.UserEntity;

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
    private static final Logger log = LoggerFactory.getLogger(BidServiceImpl.class);

    public BidServiceImpl(BidRepository bidRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.bidRepository = bidRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
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
    public List<Long> findPotentiallyOutbidUserIds(Long productId, BigDecimal latestBidAmount) {
        return bidRepository.findUserIdsOfOutbidUsers(productId, latestBidAmount);
    }

    public BidEntity findSecondHighestBid(Long productId) {
        Page<BidEntity> bids = bidRepository.findHighestBidByProductId(productId, PageRequest.of(0, 2));
        if (bids.hasContent() && bids.getContent().size() > 1) {
            return bids.getContent().get(1); // get the second highest bid
        }
        return null;
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
