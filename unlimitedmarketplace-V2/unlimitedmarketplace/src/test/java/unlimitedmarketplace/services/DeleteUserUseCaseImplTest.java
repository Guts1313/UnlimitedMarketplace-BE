package unlimitedmarketplace.services;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import unlimitedmarketplace.business.exceptions.UserNotFoundException;
import unlimitedmarketplace.business.impl.DeleteUserUseCaseImpl;
import unlimitedmarketplace.business.interfaces.DeleteUserUseCase;
import unlimitedmarketplace.persistence.repositories.UserRepository;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

 class DeleteUserUseCaseImplTest {

    @Test
     void testDeleteUser_UserExists() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        DeleteUserUseCase deleteUserUseCase = new DeleteUserUseCaseImpl(userRepository);

        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        deleteUserUseCase.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
     void testDeleteUser_UserDoesNotExist() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        DeleteUserUseCase deleteUserUseCase = new DeleteUserUseCaseImpl(userRepository);

        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> deleteUserUseCase.deleteUser(userId));
    }
}
