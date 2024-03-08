package semester3_angel_unlimitedmarketplace.security;

public interface AccessTokenEncoder {
    AccessToken encode(String accessTokenEncoded);

    String encode(AccessToken accessTokenEncoded);
}
