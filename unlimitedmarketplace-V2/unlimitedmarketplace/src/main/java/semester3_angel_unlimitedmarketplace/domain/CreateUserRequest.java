package semester3_angel_unlimitedmarketplace.domain;

import jakarta.validation.constraints.Email;
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

//comment
public class CreateUserRequest {

    @NotBlank(message = "Username cannot be empty")
    private String userName;

    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String passwordHash;

    @NotNull
    private UserRoles role;
}
