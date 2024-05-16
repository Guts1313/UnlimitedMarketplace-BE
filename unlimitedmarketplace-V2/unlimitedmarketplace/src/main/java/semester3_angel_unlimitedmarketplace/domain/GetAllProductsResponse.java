package semester3_angel_unlimitedmarketplace.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import semester3_angel_unlimitedmarketplace.persistence.entity.ProductEntity;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class GetAllProductsResponse {
    private List<ProductEntity> productEntities;
}