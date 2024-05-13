package semester3_angel_unlimitedmarketplace.business.impl;


import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import semester3_angel_unlimitedmarketplace.business.GetAllProductsUseCase;
import semester3_angel_unlimitedmarketplace.domain.GetAllProductsRequest;
import semester3_angel_unlimitedmarketplace.domain.GetAllProductsResponse;
import semester3_angel_unlimitedmarketplace.persistence.ProductRepository;
import semester3_angel_unlimitedmarketplace.persistence.entity.ProductEntity;

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
            allProducts = productRepository.findAll();
        }
        final GetAllProductsResponse response = new GetAllProductsResponse();
        List<ProductEntity> products = allProducts.stream().toList();
        response.setProductEntities(products);
        return response;
    }


}
