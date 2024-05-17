package unlimitedmarketplace.services;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import unlimitedmarketplace.business.exceptions.DuplicateEmailException;
import unlimitedmarketplace.business.exceptions.DuplicateUsernameException;
import unlimitedmarketplace.business.impl.CreateUserUseCaseImpl;
import unlimitedmarketplace.domain.CreateUserRequest;
import unlimitedmarketplace.domain.CreateUserResponse;
import unlimitedmarketplace.domain.UserRoles;
import unlimitedmarketplace.persistence.repositories.UserRepository;
import unlimitedmarketplace.persistence.entity.UserEntity;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

 class CreateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CreateUserUseCaseImpl createUserUseCase;

    @BeforeEach
     void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
     void testSaveUser_DuplicateUsername() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUserName("username");
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(new UserEntity()));

        assertThrows(DuplicateUsernameException.class, () -> createUserUseCase.saveUser(request));
    }

    @Test
     void testSaveUser_DuplicateEmail() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("email@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new UserEntity()));

        assertThrows(DuplicateEmailException.class, () -> createUserUseCase.saveUser(request));
    }

    @Test
     void testSaveUser_Success() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUserName("username");
        request.setPasswordHash("password");
        request.setEmail("email@example.com");
        request.setRole(UserRoles.USER);

        UserEntity savedUserEntity = new UserEntity();
        savedUserEntity.setId(1L);
        savedUserEntity.setUserName("username");
        savedUserEntity.setEmail("email@example.com");
        savedUserEntity.setUserRole(UserRoles.USER);

        when(userRepository.findByUserName(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUserEntity);

        CreateUserResponse response = createUserUseCase.saveUser(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("username", response.getUserName());
        assertEquals("email@example.com", response.getEmail());
        assertEquals(UserRoles.USER, response.getRole());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }
}
