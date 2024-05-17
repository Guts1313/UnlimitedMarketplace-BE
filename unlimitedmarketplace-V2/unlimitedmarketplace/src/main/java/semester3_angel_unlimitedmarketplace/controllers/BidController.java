package semester3_angel_unlimitedmarketplace.controllers;

import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import semester3_angel_unlimitedmarketplace.persistence.entity.BidEntity;
import semester3_angel_unlimitedmarketplace.security.AccessTokenEncoderDecoderImpl;

import java.math.BigDecimal;

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

    @MessageMapping("/placeBid")
    public void handleBid(BidRequest bidRequest, SimpMessageHeaderAccessor headerAccessor) {
        try {
            BidEntity bid = bidService.placeBid(bidRequest);
            BidResponse response = new BidResponse(bid.getProduct().getId(), bid.getAmount(), "success");

            // Sending bid response to the user who placed the bid
            messagingTemplate.convertAndSendToUser(headerAccessor.getUser().getName(), "/queue/bidResponse", response);

            // Broadcasting bid update to all subscribers of this product
            messagingTemplate.convertAndSend("/topic/product" + bid.getProduct().getId(), response);
        } catch (Exception e) {
            log.error("Error processing bid: {}", e.getMessage(), e);
            // Send error message back to the user
            messagingTemplate.convertAndSendToUser(headerAccessor.getUser().getName(), "/queue/bidResponse", new BidResponse(null, null, "error", e.getMessage()));
        }
    }
}

