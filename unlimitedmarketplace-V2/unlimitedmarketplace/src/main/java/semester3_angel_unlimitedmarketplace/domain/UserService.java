package semester3_angel_unlimitedmarketplace.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import semester3_angel_unlimitedmarketplace.persistence.UserRepository;
import semester3_angel_unlimitedmarketplace.persistence.entity.UserEntity;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserEntity findByUsername(String username) {
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public List<SimpleGrantedAuthority> getAuthoritiesByUsername(String username) {
        UserEntity user = findByUsername(username);
        return List.of(new SimpleGrantedAuthority(user.getUserRole(user.getId()).name()));
    }
}
