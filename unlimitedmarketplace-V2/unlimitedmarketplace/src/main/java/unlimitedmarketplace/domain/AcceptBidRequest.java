package unlimitedmarketplace.domain;

public class AcceptBidRequest {
    private Double bidAmount;
    private Long userId;

    // Getters and setters
    public Double getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(Double bidAmount) {
        this.bidAmount = bidAmount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
