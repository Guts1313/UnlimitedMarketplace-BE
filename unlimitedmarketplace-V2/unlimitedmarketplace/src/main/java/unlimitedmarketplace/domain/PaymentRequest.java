package unlimitedmarketplace.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class PaymentRequest {
    private Long productId;
    private Long userId;
    private String cardNumber;
    private String cardName;
    private String cardType;
    private String expirationDate;
    private String cvv;
    private double amount;
}
