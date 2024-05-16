package semester3_angel_unlimitedmarketplace.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface AccessTokenEncoder {
    String encode(String username, Collection<? extends GrantedAuthority> authorities);

    AccessToken encode(String accessTokenEncoded);

    String encode(AccessToken accessTokenEncoded);
}
