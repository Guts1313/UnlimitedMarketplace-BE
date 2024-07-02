package unlimitedmarketplace.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import unlimitedmarketplace.persistence.entity.ProductEntity;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class GetAllProductsResponse {
    private List<ProductEntity> productEntities;
}
