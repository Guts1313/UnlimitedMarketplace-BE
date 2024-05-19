package unlimitedmarketplace.domain;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import unlimitedmarketplace.persistence.UserRepository;
import unlimitedmarketplace.persistence.entity.UserEntity;
import java.util.List;

@Service
public class UserService {


    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity findByUsername(String username) {
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public List<SimpleGrantedAuthority> getAuthoritiesByUsername(String username) {
        UserEntity user = findByUsername(username);
        return List.of(new SimpleGrantedAuthority(user.getUserRole(user.getId()).name()));
    }
}
