package unlimitedmarketplace.business.impl;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import unlimitedmarketplace.persistence.repositories.UserRepository;
import unlimitedmarketplace.persistence.entity.UserEntity;
import java.util.Collections;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name());

        return User.builder()
                .username(user.getUserName())
                .password(user.getPasswordHash())
                .authorities(Collections.singletonList(authority))
                .build();
    }
}
