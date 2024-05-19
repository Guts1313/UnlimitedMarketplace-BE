package semester3_angel_unlimitedmarketplace.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BidResponse {
    private Long productId;
    private BigDecimal bidAmount;
    private String status;
    private String message; // Optional: A message field to pass any relevant information or errors
    private Long userId;
    public BidResponse(Long id, BigDecimal amount, String success) {
        this.productId=id;
        this.bidAmount=amount;
        this.message=success;
    }
    public BidResponse(Long id,Long userId, BigDecimal amount, String success) {
        this.productId=id;
        this.bidAmount=amount;
        this.message=success;
        this.userId=userId;
    }
    // This constructor might be specifically useful for error messages

}
