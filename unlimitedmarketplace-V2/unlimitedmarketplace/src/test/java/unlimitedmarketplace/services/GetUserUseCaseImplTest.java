package unlimitedmarketplace.services;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import unlimitedmarketplace.business.impl.GetUsersUseCaseImpl;
import unlimitedmarketplace.domain.GetAllUsersRequest;
import unlimitedmarketplace.domain.GetAllUsersResponse;
import unlimitedmarketplace.persistence.entity.UserEntity;
import unlimitedmarketplace.persistence.repositories.UserRepository;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

 class GetUserUseCaseImplTest {

    @Test
     void testGetAllUsers_WithUserName() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        GetUsersUseCaseImpl getUsersUseCase = new GetUsersUseCaseImpl(userRepository);

        String userName = "testUser";
        GetAllUsersRequest request = new GetAllUsersRequest(userName);

        List<UserEntity> userEntities = Collections.singletonList(new UserEntity());
        when(userRepository.findAllByUserName(userName)).thenReturn(userEntities);

        GetAllUsersResponse response = getUsersUseCase.getAllUsers(request);

        assertEquals(userEntities.size(), response.getAllUsers().size());
    }

    @Test
     void testGetAllUsers_WithoutUserName() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        GetUsersUseCaseImpl getUsersUseCase = new GetUsersUseCaseImpl(userRepository);

        GetAllUsersRequest request = new GetAllUsersRequest(null);

        List<UserEntity> userEntities = Collections.singletonList(new UserEntity());
        when(userRepository.findAll()).thenReturn(userEntities);

        GetAllUsersResponse response = getUsersUseCase.getAllUsers(request);

        assertEquals(userEntities.size(), response.getAllUsers().size());
    }
}
