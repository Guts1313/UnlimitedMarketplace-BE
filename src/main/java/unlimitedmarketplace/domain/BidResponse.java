package unlimitedmarketplace.domain;

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
    private String name;
    private Long userId;
    public BidResponse(Long id, BigDecimal amount, String success) {
        this.productId=id;
        this.bidAmount=amount;
        this.name =success;
    }
    public BidResponse(Long id,Long userId, BigDecimal amount, String success) {
        this.productId=id;
        this.bidAmount=amount;
        this.name =success;
        this.userId=userId;
    }

}
