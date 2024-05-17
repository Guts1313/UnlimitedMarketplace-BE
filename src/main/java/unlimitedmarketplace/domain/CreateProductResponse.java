package unlimitedmarketplace.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor

public class CreateProductResponse {
    private final Long id;
    private final String productName;
    private final String productUrl;
    private final double productPrice;
    private final String productDateCreated;

}
