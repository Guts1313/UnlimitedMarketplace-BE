package unlimitedmarketplace.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class CreateProductRequest {
    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotBlank(message = "Product name cannot be empty")
    private String productName;

    @NotBlank(message = "Product url cannot be empty")
    private String productUrl;

    @Min(value = 13,message = "Product value must be > 13")
    private double productPrice;

    @NotBlank(message = "Product creation date cannot be empty")
    private String productDateCreated;
}
