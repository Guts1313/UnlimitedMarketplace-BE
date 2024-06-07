package unlimitedmarketplace.domain;

public class AcceptBidRequest {
    private String bidAmount;
    private Long userId;

    // Getters and setters
    public String getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(String bidAmount) {
        this.bidAmount = bidAmount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
