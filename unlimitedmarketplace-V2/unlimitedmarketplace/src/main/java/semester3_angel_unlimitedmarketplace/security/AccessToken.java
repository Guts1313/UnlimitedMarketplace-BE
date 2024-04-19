package semester3_angel_unlimitedmarketplace.security;

import java.util.Set;

public interface AccessToken {
    String getSubject();

    Long getStudentId();

    Set<String> getRoles();

    boolean hasRole(String roleName);
}


