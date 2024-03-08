package semester3_angel_unlimitedmarketplace.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class LoginResponse {
    private String token;
    private String type = "Bearer";  // If you are using Bearer tokens, otherwise adjust as necessary
    public LoginResponse(String token) {
        this.token = token;
    }
}
