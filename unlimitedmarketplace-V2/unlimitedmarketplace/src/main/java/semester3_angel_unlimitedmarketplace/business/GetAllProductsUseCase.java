package semester3_angel_unlimitedmarketplace.business;

import semester3_angel_unlimitedmarketplace.domain.GetAllProductsRequest;
import semester3_angel_unlimitedmarketplace.domain.GetAllProductsResponse;
import semester3_angel_unlimitedmarketplace.domain.GetAllUsersRequest;
import semester3_angel_unlimitedmarketplace.persistence.entity.ProductEntity;

import java.util.List;

public interface GetAllProductsUseCase {
    GetAllProductsResponse getAllProducts(GetAllProductsRequest request);
}
