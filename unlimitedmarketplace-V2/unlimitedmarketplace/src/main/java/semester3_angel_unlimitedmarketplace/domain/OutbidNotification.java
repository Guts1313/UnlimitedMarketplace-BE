package semester3_angel_unlimitedmarketplace.domain;

import java.math.BigDecimal;

public class OutbidNotification {
    private Long productId;
    private BigDecimal previousBidAmount;
    private BigDecimal newBidAmount;

    public OutbidNotification(Long productId, BigDecimal previousBidAmount, BigDecimal newBidAmount) {
        this.productId = productId;
        this.previousBidAmount = previousBidAmount;
        this.newBidAmount = newBidAmount;
    }

    // Getters and setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public BigDecimal getPreviousBidAmount() {
        return previousBidAmount;
    }

    public void setPreviousBidAmount(BigDecimal previousBidAmount) {
        this.previousBidAmount = previousBidAmount;
    }

    public BigDecimal getNewBidAmount() {
        return newBidAmount;
    }

    public void setNewBidAmount(BigDecimal newBidAmount) {
        this.newBidAmount = newBidAmount;
    }
}
