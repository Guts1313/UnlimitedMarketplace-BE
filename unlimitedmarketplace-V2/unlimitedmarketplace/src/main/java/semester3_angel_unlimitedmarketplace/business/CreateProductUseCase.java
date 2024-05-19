package semester3_angel_unlimitedmarketplace.business;


import semester3_angel_unlimitedmarketplace.domain.CreateProductRequest;
import semester3_angel_unlimitedmarketplace.domain.CreateProductResponse;

public interface CreateProductUseCase {
    CreateProductResponse createProduct(CreateProductRequest request);
}
