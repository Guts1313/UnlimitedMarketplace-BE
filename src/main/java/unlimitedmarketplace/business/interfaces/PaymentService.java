package unlimitedmarketplace.business.interfaces;

import unlimitedmarketplace.domain.PaymentRequest;
import unlimitedmarketplace.persistence.entity.PaymentMethodEntity;

import java.util.List;

public interface PaymentService {
    String processPayment(PaymentRequest request);
    PaymentMethodEntity addPaymentMethod(Long userId,PaymentRequest request);
    List<PaymentMethodEntity> getUserPaymentMethods(Long userId);


}
