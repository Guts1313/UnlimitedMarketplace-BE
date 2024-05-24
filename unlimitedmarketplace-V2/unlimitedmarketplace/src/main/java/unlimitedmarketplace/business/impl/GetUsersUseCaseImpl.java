package unlimitedmarketplace.business.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private Logger logger = LoggerFactory.getLogger(GetUserUseCaseImpl.class);
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

    @Override
    public GetAllUsersResponse getAll() {
        final GetAllUsersResponse response = new GetAllUsersResponse();

        try {
            List<UserEntity> allUsers = userRepository.findAll();
            List<User> users = allUsers
                    .stream()
                    .map(UserConverter::convert)
                    .toList();
            response.setAllUsers(users);
        }catch (Exception exception){
            logger.info("Error fetching users from db", exception);
        }
        return response;
    }
}
