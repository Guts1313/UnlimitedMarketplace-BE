package semester3_angel_unlimitedmarketplace.business;

import semester3_angel_unlimitedmarketplace.domain.BidRequest;
import semester3_angel_unlimitedmarketplace.persistence.entity.BidEntity;

import java.math.BigDecimal;

public interface BidService {
    BidEntity placeBid(BidRequest bidRequest);
    BigDecimal findLatestBidAmountByProductId(Long productId);
}
