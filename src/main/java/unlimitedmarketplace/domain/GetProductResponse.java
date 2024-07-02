package unlimitedmarketplace.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import unlimitedmarketplace.persistence.entity.ProductEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetProductResponse {
    private ProductEntity productEntity;
}
