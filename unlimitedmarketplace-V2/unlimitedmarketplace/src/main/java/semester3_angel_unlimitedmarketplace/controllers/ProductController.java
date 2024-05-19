package semester3_angel_unlimitedmarketplace.controllers;

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
import semester3_angel_unlimitedmarketplace.business.CreateProductUseCase;
import semester3_angel_unlimitedmarketplace.business.GetAllProductsUseCase;
import semester3_angel_unlimitedmarketplace.business.GetProductUseCase;
import semester3_angel_unlimitedmarketplace.domain.*;
import semester3_angel_unlimitedmarketplace.security.AccessTokenEncoderDecoderImpl;

@RestController
@RequestMapping("/unlimitedmarketplace/products")
@AllArgsConstructor

public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final GetAllProductsUseCase getAllProducts;
    private final GetProductUseCase getProductUseCase;
    private static final Logger log = LoggerFactory.getLogger(AccessTokenEncoderDecoderImpl.class);

    @PreAuthorize("hasRole('ROLE_USER')")
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping
    public ResponseEntity<CreateProductResponse> createProduct(@RequestBody @Valid CreateProductRequest request) {
        CreateProductResponse response = createProductUseCase.createProduct(request);
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping
    public ResponseEntity<GetAllProductsResponse> getAllProducts(@RequestParam(value = "productCat", required = false) String productCat) {
        GetAllProductsRequest request = GetAllProductsRequest.builder().productsCat(productCat).build();
        GetAllProductsResponse response = getAllProducts.getAllProducts(request);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("Authenticated user: " + auth.getName() + " with roles: " + auth.getAuthorities());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("{id}")
    public ResponseEntity<GetProductResponse> getProduct(@PathVariable(value = "id") final Long id) {
        GetProductResponse response;
        try {
            GetProductRequest request = new GetProductRequest();
            request.setId(id);
            response = getProductUseCase.getProduct(request);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            // Optionally use auth object for further logic if needed
        } catch (Exception error) {
            // Log the error or handle it as necessary
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Return an appropriate error response
        }
        return ResponseEntity.ok(response); // Return the response entity with the retrieved product
    }

}

