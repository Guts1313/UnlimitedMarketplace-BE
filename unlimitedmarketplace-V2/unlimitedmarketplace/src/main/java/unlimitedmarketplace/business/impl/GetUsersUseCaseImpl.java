package unlimitedmarketplace.business.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import unlimitedmarketplace.business.GetUsersUseCase;
import unlimitedmarketplace.domain.GetAllUsersRequest;
import unlimitedmarketplace.domain.GetAllUsersResponse;
import unlimitedmarketplace.domain.User;
import unlimitedmarketplace.persistence.UserRepository;
import unlimitedmarketplace.persistence.entity.UserEntity;

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
