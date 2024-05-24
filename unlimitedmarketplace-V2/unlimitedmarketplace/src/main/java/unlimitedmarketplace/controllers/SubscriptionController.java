package unlimitedmarketplace.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unlimitedmarketplace.business.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@CrossOrigin(origins = "http://localhost:3000") // Replace with the URL of your React app
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private Logger mylogger = LoggerFactory.getLogger(SubscriptionController.class);

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @CrossOrigin(origins = "http://localhost:3000") // Replace with the URL of your React app
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<String>> getUserSubscriptions(@PathVariable Long userId) {
        List<String> subscriptions = subscriptionService.getUserSubscriptions(userId);
        mylogger.info("Subscriptions are: {}",subscriptions);
        return ResponseEntity.ok(subscriptions);
    }
}