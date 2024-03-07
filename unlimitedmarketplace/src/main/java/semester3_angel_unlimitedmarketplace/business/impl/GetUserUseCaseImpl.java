package semester3_angel_unlimitedmarketplace.business.impl;

import org.springframework.stereotype.Service;
import semester3_angel_unlimitedmarketplace.business.GetUserUseCase;
import semester3_angel_unlimitedmarketplace.persistence.UserRepository;
import semester3_angel_unlimitedmarketplace.domain.GetUserResponse;
import semester3_angel_unlimitedmarketplace.persistence.entity.UserEntity;

import java.util.Optional;

@Service

public class GetUserUseCaseImpl implements GetUserUseCase {
    private final UserRepository userRepository;

    public GetUserUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<GetUserResponse> getUserById(Long id) {
        UserEntity returnedUserObj = userRepository.getReferenceById(id);
        System.out.println(returnedUserObj.getEmail());
        return Optional.of(GetUserResponse.builder().id(returnedUserObj.getId()).username(returnedUserObj.getUserName()).email(returnedUserObj.getEmail()).build());
    }
}
