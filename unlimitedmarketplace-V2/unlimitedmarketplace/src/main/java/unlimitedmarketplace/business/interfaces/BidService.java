package unlimitedmarketplace.business.interfaces;

import unlimitedmarketplace.domain.BidRequest;
import unlimitedmarketplace.domain.GetMyBiddedProductsRequest;
import unlimitedmarketplace.domain.GetMyBiddedProductsResponse;
import unlimitedmarketplace.persistence.entity.BidEntity;

import java.math.BigDecimal;
import java.util.List;

public interface BidService {
    BidEntity placeBid(BidRequest bidRequest);
    GetMyBiddedProductsResponse findBiddedProductsById(GetMyBiddedProductsRequest request);
    BigDecimal findLatestBidAmountByProductId(Long productId);
    List<String> getAllBiddersExceptLatest(Long productId, String latestBidderUsername);

    BidEntity acceptBid(Long userId,BigDecimal bidAmount);
}
