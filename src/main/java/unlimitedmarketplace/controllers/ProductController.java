package unlimitedmarketplace.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import unlimitedmarketplace.business.interfaces.CreateProductUseCase;
import unlimitedmarketplace.business.interfaces.GetAllProductsUseCase;
import unlimitedmarketplace.business.interfaces.GetProductUseCase;
import unlimitedmarketplace.domain.*;

@RestController
@RequestMapping("/unlimitedmarketplace/products")
@AllArgsConstructor
@CrossOrigin(origins = "https://sem3-fe-frontend-myvoxyxc3a-lz.a.run.app")

public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final GetAllProductsUseCase getAllProducts;
    private final GetProductUseCase getProductUseCase;
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @PreAuthorize("hasRole('ROLE_USER')")
    @CrossOrigin(origins = "https://sem3-fe-frontend-myvoxyxc3a-lz.a.run.app")
    @PostMapping
    public ResponseEntity<CreateProductResponse> createProduct(@RequestBody @Valid CreateProductRequest request) {
        try {
            CreateProductResponse response = createProductUseCase.createProduct(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @CrossOrigin(origins = "https://sem3-fe-frontend-myvoxyxc3a-lz.a.run.app")
    @GetMapping
    public ResponseEntity<GetAllProductsResponse> getAllProducts(@RequestParam(value = "productCat", required = false) String productCat) {
        GetAllProductsRequest request = GetAllProductsRequest.builder().productsCat(productCat).build();
        GetAllProductsResponse response = getAllProducts.getAllProducts(request);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("Authenticated user: {}" , auth.getName() + " with roles: {}" + auth.getAuthorities());
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasRole('ROLE_USER')")
    @CrossOrigin(origins = "https://sem3-fe-frontend-myvoxyxc3a-lz.a.run.app")
    @GetMapping("/mylistings")
    public ResponseEntity<GetAllProductsResponse> getUserListedProducts(@RequestParam(value = "userId") Long userId) {
        try {
            GetAllProductsRequest request = GetAllProductsRequest.builder().id(userId).build();
            GetAllProductsResponse listedProducts = getAllProducts.getAllListedProductsByUserId(request);
            return ResponseEntity.ok(listedProducts);
        } catch (Exception e) {
            log.error("Failed to fetch listed products: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PreAuthorize("hasRole('ROLE_USER')")
    @CrossOrigin(origins = "https://sem3-fe-frontend-myvoxyxc3a-lz.a.run.app")
    @GetMapping("{id}")
    public ResponseEntity<GetProductResponse> getProduct(@PathVariable(value = "id") final Long id) {
        GetProductResponse response;
        try {
            GetProductRequest request = new GetProductRequest();
            request.setId(id);
            response = getProductUseCase.getProduct(request);
        } catch (Exception error) {
            // Log the error or handle it as necessary
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Return an appropriate error response
        }
        return ResponseEntity.ok(response); // Return the response entity with the retrieved product
    }

}

