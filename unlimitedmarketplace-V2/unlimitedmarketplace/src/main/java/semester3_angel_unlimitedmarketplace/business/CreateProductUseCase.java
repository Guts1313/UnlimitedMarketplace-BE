package semester3_angel_unlimitedmarketplace.business;


import org.springframework.stereotype.Service;
import semester3_angel_unlimitedmarketplace.domain.CreateProductRequest;
import semester3_angel_unlimitedmarketplace.domain.CreateProductResponse;
import semester3_angel_unlimitedmarketplace.domain.CreateUserResponse;

public interface CreateProductUseCase {
    CreateProductResponse createProduct(CreateProductRequest request);
}
