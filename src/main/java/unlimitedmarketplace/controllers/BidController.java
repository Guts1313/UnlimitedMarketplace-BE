package unlimitedmarketplace.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import unlimitedmarketplace.business.interfaces.BidService;
import unlimitedmarketplace.business.impl.SubscriptionService;
import unlimitedmarketplace.domain.*;
import unlimitedmarketplace.persistence.repositories.UserRepository;
import unlimitedmarketplace.persistence.entity.BidEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/bids")
@CrossOrigin(origins = "https://sem3-fe-frontend-myvoxyxc3a-lz.a.run.app")

public class BidController {

    private final SimpMessagingTemplate messagingTemplate;
    private final BidService bidService;
    private static final Logger log = LoggerFactory.getLogger(BidController.class);
    private final SubscriptionService subscriptionService;
    private static final String QUEUE_WINNER = "/queue/winner";  // Compliant

    public BidController(SimpMessagingTemplate messagingTemplate, BidService bidService, SubscriptionService subscriptionService, UserRepository userRepository) {
        this.messagingTemplate = messagingTemplate;
        this.bidService = bidService;
        this.subscriptionService = subscriptionService;
    }


    @GetMapping("/latest/{productId}")
    public ResponseEntity<BidResponse> getLatestBid(@PathVariable Long productId) {
        try {
            BigDecimal latestBidAmount = bidService.findLatestBidAmountByProductId(productId);
            return ResponseEntity.ok(new BidResponse(productId, latestBidAmount, "success"));
        } catch (Exception e) {
            log.error("Failed to fetch latest bid: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @MessageMapping("/notification")
    public void subscribeToNotifications(String message) {

    }

    @MessageMapping("/placeBid")
    public void handleBid(BidRequest bidRequest, SimpMessageHeaderAccessor headerAccessor) {
        if (bidRequest == null) {
            throw new IllegalArgumentException("Bid request cannot be null.");
        }
        if (headerAccessor == null) {
            throw new IllegalArgumentException("Header accessor cannot be null.");
        }
        try {
            BidEntity bid = bidService.placeBid(bidRequest);

            if (bid == null) {
                throw new IllegalStateException("Bid creation failed, no bid returned.");
            }
            if (bid.getProduct() == null || bid.getProduct().getId() == null) {
                throw new IllegalStateException("Bid product or product ID is null.");
            }
            BigDecimal latestBidAmount = bid.getAmount();
            Long productId = bid.getProduct().getId();
            Long userId = bid.getUser().getId();
            Principal principal = headerAccessor.getUser();

            if (userId == null) {
                throw new IllegalStateException("Bid user ID is null.");
            }
            BidResponse bidResponse = new BidResponse(productId, userId, latestBidAmount, "success");
            subscriptionService.addUserSubscription(userId, "/topic/product" + productId);
            subscriptionService.addUserSubscription(userId, "/queue/outbid" + productId);
            subscriptionService.addUserSubscription(userId, QUEUE_WINNER + productId);
            messagingTemplate.convertAndSend("/topic/product" + productId, bidResponse);

            // Get the latest bid details
            BigDecimal highestBidAmount = bidService.findLatestBidAmountByProductId(productId);

            if (principal != null) {
                String latestBidderUsername = principal.getName();
                List<String> allBiddersExceptLatest = bidService.getAllBiddersExceptLatest(productId, latestBidderUsername);

                Set<String> uniqueBidders = new HashSet<>(allBiddersExceptLatest);
                for (String bidder : uniqueBidders) {
                    if (!bidder.equals(latestBidderUsername)) { // Avoid self-notification
                        messagingTemplate.convertAndSendToUser(bidder, "/queue/outbid" + productId, highestBidAmount.toString());
                        log.info("Bid sent :{}", highestBidAmount.toString());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error processing bid: {}", e.getMessage(), e);
            if (headerAccessor != null && headerAccessor.getUser() != null) {
                messagingTemplate.convertAndSendToUser(headerAccessor.getUser().getName(), "/queue/bidResponse", new BidResponse(null, null, BigDecimal.ONE, e.getMessage()));
            } else {
                log.error("Header accessor or user is null: Cannot send error response to user.");
            }
        }
    }

    @MessageMapping("/acceptBid")
    public void acceptBid(AcceptBidRequest acceptBidRequest) {
        try {
            String bidAmount = acceptBidRequest.getBidAmount();
            Long userId = acceptBidRequest.getUserId();
            BigDecimal bidAmountBigDecimal = new BigDecimal(bidAmount).setScale(2, RoundingMode.HALF_UP);
            log.info("Searching for bid with amount: {} and userId: {}", bidAmountBigDecimal, userId);
            BidEntity bid = bidService.acceptBid(userId, bidAmountBigDecimal);
            if (bid != null) {
                subscriptionService.addUserSubscription(userId, QUEUE_WINNER + bid.getProduct().getId());
                messagingTemplate.convertAndSendToUser(bid.getUser().getUserName(), QUEUE_WINNER + bid.getProduct().getId(), bid.getAmount().toString());
                log.info("Bid sent to user:{}", bid.getUser().getUserName());
                log.info("Bid sent for prod id:{}", bid.getProduct().getProductName());


            }
        } catch (Exception e) {
            log.error("Error accepting bid: {}", e.getMessage(), e);
        }
    }


    @GetMapping("/user-bids/{userId}")
    public ResponseEntity<GetMyBiddedProductsResponse> getUserBids(@PathVariable Long userId) {
        try {
            GetMyBiddedProductsRequest request = GetMyBiddedProductsRequest.builder().userId(userId).build();
            GetMyBiddedProductsResponse userBidProducts = bidService.findBiddedProductsById(request);
            BigDecimal totalBidAmount = bidService.getTotalBidAmountByUserId(userId);
            userBidProducts.setTotalBidAmount(totalBidAmount);
            return ResponseEntity.ok(userBidProducts);
        } catch (Exception e) {
            log.error("Failed to fetch user bids: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

}


