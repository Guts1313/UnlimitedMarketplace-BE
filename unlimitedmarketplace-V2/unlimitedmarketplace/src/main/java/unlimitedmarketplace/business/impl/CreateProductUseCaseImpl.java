package unlimitedmarketplace.business.impl;


import org.springframework.stereotype.Service;
import unlimitedmarketplace.business.CreateProductUseCase;
import unlimitedmarketplace.domain.CreateProductRequest;
import unlimitedmarketplace.domain.CreateProductResponse;
import unlimitedmarketplace.domain.ProductStatus;
import unlimitedmarketplace.persistence.ProductRepository;
import unlimitedmarketplace.persistence.UserRepository;
import unlimitedmarketplace.persistence.entity.ProductEntity;
import unlimitedmarketplace.persistence.entity.UserEntity;

@Service
public class CreateProductUseCaseImpl implements CreateProductUseCase {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;


    public CreateProductUseCaseImpl(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CreateProductResponse createProduct(CreateProductRequest request) {
        UserEntity userEntity = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + request.getUserId()));

        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductName(request.getProductName());
        productEntity.setProductPrice(request.getProductPrice());
        productEntity.setProductUrl(request.getProductUrl());
        productEntity.setProductStatus(ProductStatus.ACTIVE.toString());

        productEntity.setProductDateAdded(request.getProductDateCreated());
        productEntity.setUser(userEntity);  // Set the user here

        ProductEntity savedProductEntity = productRepository.save(productEntity);

        return new CreateProductResponse(savedProductEntity.getId(), savedProductEntity.getProductUrl(), savedProductEntity.getProductName(), savedProductEntity.getProductPrice(), savedProductEntity.getProductDateAdded());
    }

}
