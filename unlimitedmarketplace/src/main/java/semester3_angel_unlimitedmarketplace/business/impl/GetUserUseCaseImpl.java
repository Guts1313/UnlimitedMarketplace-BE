package semester3_angel_unlimitedmarketplace.business.impl;

import org.springframework.stereotype.Service;
import semester3_angel_unlimitedmarketplace.business.GetUserUseCase;
import semester3_angel_unlimitedmarketplace.business.customexceptions.UserNotFoundException;
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

    public GetUserResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> GetUserResponse.builder()
                        .id(user.getId())
                        .username(user.getUserName())
                        .email(user.getEmail())
                        .build())
                .orElseThrow(() -> new UserNotFoundException(id));
    }

}
