package semester3_angel_unlimitedmarketplace.security;

public interface AccessTokenDecoder {
    AccessToken decode(String accessTokenEncoded);

}
