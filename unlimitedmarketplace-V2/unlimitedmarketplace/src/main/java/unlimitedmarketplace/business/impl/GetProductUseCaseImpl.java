package unlimitedmarketplace.business.impl;


import org.springframework.stereotype.Service;
import unlimitedmarketplace.business.interfaces.GetProductUseCase;
import unlimitedmarketplace.domain.GetProductRequest;
import unlimitedmarketplace.domain.GetProductResponse;
import unlimitedmarketplace.persistence.repositories.ProductRepository;
import unlimitedmarketplace.persistence.entity.ProductEntity;

import java.util.Optional;

@Service
public class GetProductUseCaseImpl implements GetProductUseCase {
    private final ProductRepository productRepository;

    public GetProductUseCaseImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public GetProductResponse getProduct(GetProductRequest request) {
        GetProductResponse response = new GetProductResponse();

        // Fetch the product using the repository
        Optional<ProductEntity> productEntity = productRepository.findById(request.getId());

        // Check if product was found
        if (productEntity.isPresent()) {
            // Set the product entity in the response if it is present
            response.setProductEntity(productEntity.get());
        } else {
            return null;
        }

        // Return the response
        return response;

    }
}
