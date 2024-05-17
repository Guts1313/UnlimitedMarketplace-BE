package unlimitedmarketplace.security;

import io.jsonwebtoken.Claims;

public interface AccessTokenDecoder {
    Claims decode(String accessTokenEncoded) throws InvalidAccessTokenException;

    AccessToken decodeEncoded(String accessTokenString);
}
