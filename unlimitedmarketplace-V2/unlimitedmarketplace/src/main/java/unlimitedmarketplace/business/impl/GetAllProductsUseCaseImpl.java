package unlimitedmarketplace.business.impl;


import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import unlimitedmarketplace.business.interfaces.GetAllProductsUseCase;
import unlimitedmarketplace.domain.GetAllProductsRequest;
import unlimitedmarketplace.domain.GetAllProductsResponse;
import unlimitedmarketplace.domain.ProductStatus;
import unlimitedmarketplace.persistence.repositories.ProductRepository;
import unlimitedmarketplace.persistence.entity.ProductEntity;

import java.util.List;

@Service
public class GetAllProductsUseCaseImpl implements GetAllProductsUseCase {
    private final ProductRepository productRepository;

    public GetAllProductsUseCaseImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public GetAllProductsResponse getAllProducts(final GetAllProductsRequest request) {
        List<ProductEntity> allProducts;
        if (StringUtils.hasText(request.getProductsCat())) {
            allProducts = productRepository.findProductEntitiesByProductNameLike(request.getProductsCat());
        } else {
            allProducts = productRepository.findAllByProductStatus(String.valueOf(ProductStatus.valueOf(ProductStatus.ACTIVE.toString())));
        }
        final GetAllProductsResponse response = new GetAllProductsResponse();
        List<ProductEntity> products = allProducts.stream().toList();
        response.setProductEntities(products);
        return response;
    }
    public GetAllProductsResponse getAllListedProductsByUserId(final GetAllProductsRequest request) {
        List<ProductEntity> products = productRepository.findListedByUserId(request.getId());
        final GetAllProductsResponse response = new GetAllProductsResponse();
        response.setProductEntities(products);
        return response;
    }

}
