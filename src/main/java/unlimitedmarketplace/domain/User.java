package unlimitedmarketplace.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor

public class User {
    private Long id;
    private String userName;
    private String passwordHash;
    private String email;
    private UserRoles role;

}
