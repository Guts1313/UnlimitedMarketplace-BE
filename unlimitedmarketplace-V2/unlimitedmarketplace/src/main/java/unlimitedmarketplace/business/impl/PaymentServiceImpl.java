package unlimitedmarketplace.business.impl;

import org.springframework.stereotype.Service;
import unlimitedmarketplace.business.interfaces.PaymentService;
import unlimitedmarketplace.business.exceptions.UserNotFoundException;
import unlimitedmarketplace.domain.PaymentRequest;
import unlimitedmarketplace.domain.ProductPaymentStatus;
import unlimitedmarketplace.persistence.repositories.PaymentMethodRepository;
import unlimitedmarketplace.persistence.repositories.ProductRepository;
import unlimitedmarketplace.persistence.repositories.TransactionRepository;
import unlimitedmarketplace.persistence.repositories.UserRepository;
import unlimitedmarketplace.persistence.entity.PaymentMethodEntity;
import unlimitedmarketplace.persistence.entity.ProductEntity;
import unlimitedmarketplace.persistence.entity.TransactionEntity;
import unlimitedmarketplace.persistence.entity.UserEntity;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;
    private final Random random;

    private static final String ALGORITHM = "AES";

    public PaymentServiceImpl(PaymentMethodRepository paymentMethodRepository, UserRepository userRepository, TransactionRepository transactionRepository, ProductRepository productRepository, Random random) {
        this.paymentMethodRepository = paymentMethodRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.productRepository = productRepository;
        this.random = random;
    }

    public String processPayment(PaymentRequest request) {
        boolean paymentSuccess = random.nextBoolean();
        if (paymentSuccess) {
            addTransaction(request.getUserId(), request, ProductPaymentStatus.PAID.toString());
            ProductEntity product = productRepository.findProductEntityById(request.getProductId());
            product.setPaymentStatus(ProductPaymentStatus.PAID.toString());
            productRepository.saveAndFlush(product);
            return UUID.randomUUID().toString(); // Return a mock transaction ID
        } else {
            addTransaction(request.getUserId(), request, ProductPaymentStatus.AWAITING.toString());
            throw new RuntimeException("Insufficient funds");
        }
    }


    public PaymentMethodEntity addPaymentMethod(Long userId, PaymentRequest request) {
        String encryptedCvv = encryptCvv(request.getCvv());

        PaymentMethodEntity paymentMethod = PaymentMethodEntity.builder()
                .user(UserEntity.builder().id(userId).build())
                .cardType(request.getCardType())
                .cardNumber(maskCardNumber(request.getCardNumber()))
                .cardName(request.getCardName())
                .expirationDate(request.getExpirationDate())
                .encryptedCvv(encryptedCvv)
                .build();

        return paymentMethodRepository.save(paymentMethod);
    }

    public List<PaymentMethodEntity> getUserPaymentMethods(Long userId) {
        return paymentMethodRepository.findByUserId(userId);
    }

    private String encryptCvv(String cvv) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(128);
            SecretKey secretKey = keyGen.generateKey();
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(cvv.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting CVV", e);
        }
    }

    private String maskCardNumber(String cardNumber) {
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    private TransactionEntity addTransaction(Long userId, PaymentRequest paymentRequest, String status) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException());
        TransactionEntity transaction = TransactionEntity.builder()
                .user(user)
                .cardType(paymentRequest.getCardType())
                .cardNumber(maskCardNumber(paymentRequest.getCardNumber()))
                .amount(paymentRequest.getAmount())
                .timestamp(LocalDateTime.now())
                .status(status)
                .build();
        return transactionRepository.save(transaction);
    }
}
