package semester3_angel_unlimitedmarketplace.business.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import semester3_angel_unlimitedmarketplace.business.GetUsersUseCase;
import semester3_angel_unlimitedmarketplace.domain.GetAllUsersRequest;
import semester3_angel_unlimitedmarketplace.domain.GetAllUsersResponse;
import semester3_angel_unlimitedmarketplace.domain.User;
import semester3_angel_unlimitedmarketplace.persistence.UserRepository;
import semester3_angel_unlimitedmarketplace.persistence.entity.UserEntity;

import java.util.List;

@Service
public class GetUsersUseCaseImpl implements GetUsersUseCase {
private final UserRepository userRepository;

    public GetUsersUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public GetAllUsersResponse getAllUsers(final GetAllUsersRequest request) {
        List<UserEntity> allUsers;
        if (StringUtils.hasText(request.getUserName())){
            allUsers = userRepository.findAllByUserName(request.getUserName());
        }
        else{
            allUsers=userRepository.findAll();

        }
        final GetAllUsersResponse response = new GetAllUsersResponse();
        List<User> users = allUsers
                .stream()
                .map(UserConverter::convert)
                .toList();
        response.setAllUsers(users);
        return response;

    }
}
