package unlimitedmarketplace.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import unlimitedmarketplace.business.interfaces.PaymentService;
import unlimitedmarketplace.domain.PaymentRequest;
import unlimitedmarketplace.domain.PaymentResponse;
import unlimitedmarketplace.domain.UserService;
import unlimitedmarketplace.persistence.entity.PaymentMethodEntity;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final UserService userService;

    public PaymentController(PaymentService paymentService, UserService userService) {
        this.paymentService = paymentService;
        this.userService = userService;
    }

    @PostMapping("/add")
    public ResponseEntity<PaymentMethodEntity> addPaymentMethod(@RequestBody PaymentRequest request, Authentication authentication) {
        Long userId = userService.findByUsername(authentication.getName()).getId();
        PaymentMethodEntity paymentMethod = paymentService.addPaymentMethod(userId, request);
        return ResponseEntity.ok(paymentMethod);
    }

    @GetMapping("/listpaymentoptions")
    public ResponseEntity<List<PaymentMethodEntity>> getPaymentMethods(Authentication authentication) {
        Long userId = userService.findByUsername(authentication.getName()).getId();
        List<PaymentMethodEntity> paymentMethods = paymentService.getUserPaymentMethods(userId);
        return ResponseEntity.ok(paymentMethods);
    }

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest request, Authentication authentication) {
        try {
            Long userId = userService.findByUsername(authentication.getName()).getId();
            String transactionId = paymentService.processPayment(request);
            return ResponseEntity.ok(new PaymentResponse(transactionId, "Payment successful" + userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(new PaymentResponse(null, e.getMessage()));
        }
    }
}
