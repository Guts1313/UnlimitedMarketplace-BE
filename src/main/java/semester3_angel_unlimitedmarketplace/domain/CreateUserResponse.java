package semester3_angel_unlimitedmarketplace.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor

public class CreateUserResponse {
    private Long id;
    private final String userName;
    private final String email;


}
