package semester3_angel_unlimitedmarketplace.security;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collections;
import java.util.Set;
@Getter
@EqualsAndHashCode
public class AccessTokenImpl implements AccessToken{
    private final String subject;
    private final Long userId;
    private final Set<String> roles;

    public AccessTokenImpl(String subject, Long studentId, Set<String> roles) {
        this.subject = subject;
        this.userId = studentId;
        this.roles = roles != null ? Set.copyOf(roles) : Collections.emptySet();
    }

    @Override
    public boolean hasRole(String roleName) {
        return this.roles.contains(roleName);
    }

}
