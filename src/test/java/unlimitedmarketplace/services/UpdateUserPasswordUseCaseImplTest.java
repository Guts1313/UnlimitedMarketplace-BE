package unlimitedmarketplace.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import unlimitedmarketplace.business.exceptions.InvalidUserException;
import unlimitedmarketplace.business.impl.UpdateUserPasswordUseCaseImpl;
import unlimitedmarketplace.domain.UpdateUserPasswordRequest;
import unlimitedmarketplace.persistence.repositories.UserRepository;
import unlimitedmarketplace.persistence.entity.UserEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

 class UpdateUserPasswordUseCaseImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UpdateUserPasswordUseCaseImpl updateUserPasswordUseCase;

    @BeforeEach
     void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
     void testUpdatePassword_UserNotFound() {
        UpdateUserPasswordRequest request = new UpdateUserPasswordRequest();
        request.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(InvalidUserException.class, () -> updateUserPasswordUseCase.updatePassword(request));
    }

    @Test
     void testUpdatePassword_Success() {
        UpdateUserPasswordRequest request = new UpdateUserPasswordRequest();
        request.setId(1L);
        request.setNewPassword("newPassword");

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setPasswordHash("oldPasswordHash");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("newPasswordHash");

        updateUserPasswordUseCase.updatePassword(request);

        assertEquals("newPasswordHash", user.getPasswordHash());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }
}
