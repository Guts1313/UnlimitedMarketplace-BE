package unlimitedmarketplace.business.interfaces;

import unlimitedmarketplace.domain.GetProductRequest;
import unlimitedmarketplace.domain.GetProductResponse;

public interface GetProductUseCase {
    GetProductResponse getProduct(GetProductRequest request);
}
