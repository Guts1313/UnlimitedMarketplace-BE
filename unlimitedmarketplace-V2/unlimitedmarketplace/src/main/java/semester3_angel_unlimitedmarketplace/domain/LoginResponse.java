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
    private String accessToken;
    private String refreshToken;

    private String type = "Bearer";  // If you are using Bearer tokens, otherwise adjust as necessary

    public LoginResponse(String token,String refreshToken) {
        this.accessToken = token;
        this.refreshToken=refreshToken;
    }
}
