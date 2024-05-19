package semester3_angel_unlimitedmarketplace.controllers;

import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import semester3_angel_unlimitedmarketplace.business.BidService;
import semester3_angel_unlimitedmarketplace.domain.BidRequest;
import semester3_angel_unlimitedmarketplace.domain.BidResponse;
import semester3_angel_unlimitedmarketplace.domain.OutbidNotification;
import semester3_angel_unlimitedmarketplace.persistence.entity.BidEntity;
import semester3_angel_unlimitedmarketplace.security.AccessTokenEncoderDecoderImpl;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

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
    public ResponseEntity<?> getLatestBid(@PathVariable Long productId) {
        try {
            BigDecimal latestBidAmount = bidService.findLatestBidAmountByProductId(productId);
            return ResponseEntity.ok(new BidResponse(productId, latestBidAmount, "success"));
        } catch (Exception e) {
            log.error("Failed to fetch latest bid: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch latest bid");
        }
    }

    @MessageMapping("/notification")
    public void subscribeToNotifications(String message) {
        // This method is a placeholder to ensure subscription can be established.
        // Actual notifications will be sent from elsewhere in the application logic

    }

    @MessageMapping("/placeBid")
    public void handleBid(BidRequest bidRequest, SimpMessageHeaderAccessor headerAccessor) {
        try {
            if (bidRequest == null) {
                throw new IllegalArgumentException("Bid request cannot be null.");
            }

            BidEntity bid = bidService.placeBid(bidRequest);

            if (bid == null) {
                throw new IllegalStateException("Bid creation failed, no bid returned.");
            }

            BigDecimal latestBidAmount = bid.getAmount();
            Long productId = bid.getProduct().getId();
            Principal principal = headerAccessor.getUser();
            BidResponse bidResponse = new BidResponse(productId, bid.getUser().getId(), latestBidAmount, "success");
            messagingTemplate.convertAndSend("/topic/product" + productId, bidResponse);

            if (principal != null) {
                String latestBidderUsername = principal.getName();

                // Fetch all users who have placed bids on this product except the latest bidder
                List<String> allBiddersExceptLatest = bidService.getAllBiddersExceptLatest(productId, latestBidderUsername);

                for (String bidder : allBiddersExceptLatest) {
                    messagingTemplate.convertAndSendToUser(bidder, "/queue/outbid" + productId, bidResponse.getBidAmount().toString());
                }
            }
        } catch (Exception e) {
            log.error("Error processing bid: {}", e.getMessage(), e);
            if (headerAccessor.getUser() != null) {
                messagingTemplate.convertAndSendToUser(headerAccessor.getUser().getName(), "/queue/bidResponse", new BidResponse(null, null, BigDecimal.ONE, e.getMessage()));
            }
        }
    }
}

