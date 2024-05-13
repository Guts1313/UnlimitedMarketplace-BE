package semester3_angel_unlimitedmarketplace.business.impl;


import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import semester3_angel_unlimitedmarketplace.business.CreateProductUseCase;
import semester3_angel_unlimitedmarketplace.domain.CreateProductRequest;
import semester3_angel_unlimitedmarketplace.domain.CreateProductResponse;
import semester3_angel_unlimitedmarketplace.persistence.ProductRepository;
import semester3_angel_unlimitedmarketplace.persistence.entity.ProductEntity;

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
