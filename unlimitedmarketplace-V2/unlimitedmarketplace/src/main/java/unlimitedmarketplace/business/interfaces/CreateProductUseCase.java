package unlimitedmarketplace.business.interfaces;


import unlimitedmarketplace.domain.CreateProductRequest;
import unlimitedmarketplace.domain.CreateProductResponse;

public interface CreateProductUseCase {
    CreateProductResponse createProduct(CreateProductRequest request);
}
