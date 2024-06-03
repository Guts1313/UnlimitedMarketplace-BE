package unlimitedmarketplace.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetAllProductsRequest {
    private String productsCat;
    private Long id;


    public GetAllProductsRequest(Long userId) {
        id = userId;
        this.productsCat = "";
    }

}
