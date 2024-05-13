package semester3_angel_unlimitedmarketplace.business;

import semester3_angel_unlimitedmarketplace.domain.GetProductRequest;
import semester3_angel_unlimitedmarketplace.domain.GetProductResponse;

public interface GetProductUseCase {
    GetProductResponse getProduct(GetProductRequest request);
}
