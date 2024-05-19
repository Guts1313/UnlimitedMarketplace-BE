package semester3_angel_unlimitedmarketplace.business;

import semester3_angel_unlimitedmarketplace.domain.GetAllProductsRequest;
import semester3_angel_unlimitedmarketplace.domain.GetAllProductsResponse;

public interface GetAllProductsUseCase {
    GetAllProductsResponse getAllProducts(GetAllProductsRequest request);
}
