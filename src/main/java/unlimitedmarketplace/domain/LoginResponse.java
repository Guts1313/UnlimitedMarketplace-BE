package unlimitedmarketplace.domain;

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
    private String type = "Bearer";
    private Long userId;

    public LoginResponse(String token, String refreshToken, Long userId) {
        this.accessToken = token;
        this.refreshToken = refreshToken;
        this.userId = userId;

    }
}
