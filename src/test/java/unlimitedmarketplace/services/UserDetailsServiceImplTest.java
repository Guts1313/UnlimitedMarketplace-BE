package unlimitedmarketplace.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import unlimitedmarketplace.business.impl.UserDetailsServiceImpl;
import unlimitedmarketplace.domain.UserRoles;
import unlimitedmarketplace.persistence.repositories.UserRepository;
import unlimitedmarketplace.persistence.entity.UserEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

 class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
     void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
     void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("username"));
    }

    @Test
     void testLoadUserByUsername_Success() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName("username");
        userEntity.setPasswordHash("passwordHash");
        userEntity.setUserRole(UserRoles.USER);

        when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(userEntity));

        UserDetails userDetails = userDetailsService.loadUserByUsername("username");

        assertNotNull(userDetails);
        assertEquals("username", userDetails.getUsername());
        assertEquals("passwordHash", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER")));
    }
}
