package unlimitedmarketplace.business.impl;

import org.springframework.stereotype.Service;
import unlimitedmarketplace.business.interfaces.GetUserUseCase;
import unlimitedmarketplace.business.exceptions.UserNotFoundException;
import unlimitedmarketplace.persistence.repositories.UserRepository;
import unlimitedmarketplace.domain.GetUserResponse;


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
                        .role(user.getUserRole().toString())
                        .build())
                .orElseThrow(UserNotFoundException::new);
    }

}
