package unlimitedmarketplace.business.impl;


import org.springframework.stereotype.Service;
import unlimitedmarketplace.business.CreateProductUseCase;
import unlimitedmarketplace.domain.CreateProductRequest;
import unlimitedmarketplace.domain.CreateProductResponse;
import unlimitedmarketplace.persistence.ProductRepository;
import unlimitedmarketplace.persistence.entity.ProductEntity;

@Service
public class CreateProductUseCaseImpl implements CreateProductUseCase {
    private final ProductRepository productRepository;

    public CreateProductUseCaseImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public CreateProductResponse createProduct(CreateProductRequest request) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductName(request.getProductName());
        productEntity.setProductPrice(request.getProductPrice());
        productEntity.setProductUrl(request.getProductUrl());
        productEntity.setProductDateAdded(request.getProductDateCreated());
        ProductEntity savedProductEntity = productRepository.save(productEntity);

        return new CreateProductResponse(savedProductEntity.getId(), savedProductEntity.getProductUrl(), savedProductEntity.getProductName(), savedProductEntity.getProductPrice(), savedProductEntity.getProductDateAdded());
    }
}
