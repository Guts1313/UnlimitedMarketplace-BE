package unlimitedmarketplace.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unlimitedmarketplace.business.impl.GetUsersUseCaseImpl;
import unlimitedmarketplace.domain.GetAllUsersRequest;
import unlimitedmarketplace.domain.GetAllUsersResponse;
import unlimitedmarketplace.persistence.repositories.UserRepository;
import unlimitedmarketplace.persistence.entity.UserEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

 class GetUsersUseCaseImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetUsersUseCaseImpl getUsersUseCase;

    private Logger logger = LoggerFactory.getLogger(GetUsersUseCaseImpl.class);

    @BeforeEach
     void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
     void testGetAllUsersByUserName() {
        GetAllUsersRequest request = new GetAllUsersRequest();
        request.setUserName("JohnDoe");

        List<UserEntity> users = List.of(new UserEntity(), new UserEntity());
        when(userRepository.findAllByUserName(anyString())).thenReturn(users);

        GetAllUsersResponse response = getUsersUseCase.getAllUsers(request);

        assertNotNull(response);
        assertEquals(2, response.getAllUsers().size());
        verify(userRepository, times(1)).findAllByUserName(anyString());
    }

    @Test
     void testGetAllUsers() {
        GetAllUsersRequest request = new GetAllUsersRequest();

        List<UserEntity> users = List.of(new UserEntity(), new UserEntity());
        when(userRepository.findAll()).thenReturn(users);

        GetAllUsersResponse response = getUsersUseCase.getAllUsers(request);

        assertNotNull(response);
        assertEquals(2, response.getAllUsers().size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
     void testGetAll() {
        List<UserEntity> users = List.of(new UserEntity(), new UserEntity());
        when(userRepository.findAll()).thenReturn(users);

        GetAllUsersResponse response = getUsersUseCase.getAll();

        assertNotNull(response);
        assertEquals(2, response.getAllUsers().size());
        verify(userRepository, times(1)).findAll();
    }
}
