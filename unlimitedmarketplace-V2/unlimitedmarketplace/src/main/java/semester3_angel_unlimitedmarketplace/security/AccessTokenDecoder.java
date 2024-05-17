package semester3_angel_unlimitedmarketplace.security;

import io.jsonwebtoken.Claims;

public interface AccessTokenDecoder {
    Claims decode(String accessTokenEncoded) throws InvalidAccessTokenException;

    AccessToken decodeEncoded(String accessTokenString);
}
