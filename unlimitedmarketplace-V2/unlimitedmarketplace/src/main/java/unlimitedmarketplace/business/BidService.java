package unlimitedmarketplace.business;

import unlimitedmarketplace.domain.BidRequest;
import unlimitedmarketplace.persistence.entity.BidEntity;

import java.math.BigDecimal;
import java.util.List;

public interface BidService {
    BidEntity placeBid(BidRequest bidRequest);
    BigDecimal findLatestBidAmountByProductId(Long productId);
    BidEntity findSecondHighestBid(Long productId);
    List<Long> findPotentiallyOutbidUserIds(Long productId, BigDecimal latestBidAmount);
    List<String> getAllBiddersExceptLatest(Long productId, String latestBidderUsername);
}
