package unlimitedmarketplace.business;

import unlimitedmarketplace.domain.GetAllProductsRequest;
import unlimitedmarketplace.domain.GetAllProductsResponse;

public interface GetAllProductsUseCase {
    GetAllProductsResponse getAllProducts(GetAllProductsRequest request);
    GetAllProductsResponse getAllListedProductsByUserId(final GetAllProductsRequest request);
}
