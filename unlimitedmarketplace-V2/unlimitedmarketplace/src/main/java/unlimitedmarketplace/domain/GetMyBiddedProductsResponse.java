package unlimitedmarketplace.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import unlimitedmarketplace.persistence.entity.BidEntity;
import unlimitedmarketplace.persistence.entity.ProductEntity;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetMyBiddedProductsResponse {
    private List<BidEntity> userBidProducts;
    private Long userId;
}
