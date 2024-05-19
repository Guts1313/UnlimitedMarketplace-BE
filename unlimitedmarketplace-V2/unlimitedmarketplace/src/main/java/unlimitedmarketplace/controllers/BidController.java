package unlimitedmarketplace.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import unlimitedmarketplace.business.BidService;
import unlimitedmarketplace.domain.BidRequest;
import unlimitedmarketplace.domain.BidResponse;
import unlimitedmarketplace.persistence.entity.BidEntity;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/bids")
public class BidController {

    private final SimpMessagingTemplate messagingTemplate;
    private final BidService bidService;
    private static final Logger log = LoggerFactory.getLogger(BidController.class);

    public BidController(SimpMessagingTemplate messagingTemplate, BidService bidService) {
        this.messagingTemplate = messagingTemplate;
        this.bidService = bidService;
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
        // This method is a placeholder to ensure subscription can be established.
        // Actual notifications will be sent from elsewhere in the application logic

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
            Principal principal = headerAccessor.getUser();

            if (bid.getUser() == null || bid.getUser().getId() == null) {
                throw new IllegalStateException("Bid user or user ID is null.");
            }

            BidResponse bidResponse = new BidResponse(productId, bid.getUser().getId(), latestBidAmount, "success");
            messagingTemplate.convertAndSend("/topic/product" + productId, bidResponse);

            if (principal != null) {
                String latestBidderUsername = principal.getName();
                List<String> allBiddersExceptLatest = bidService.getAllBiddersExceptLatest(productId, latestBidderUsername);

                // Ensure we only send unique notifications to each user
                Set<String> uniqueBidders = new HashSet<>(allBiddersExceptLatest);
                for (String bidder : uniqueBidders) {
                    messagingTemplate.convertAndSendToUser(bidder, "/queue/outbid" + productId, latestBidAmount.toString());
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


}


