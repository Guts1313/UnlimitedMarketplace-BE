package unlimitedmarketplace.domain;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class AcceptBidRequest {
    private String bidAmount;
    private Long userId;
}
