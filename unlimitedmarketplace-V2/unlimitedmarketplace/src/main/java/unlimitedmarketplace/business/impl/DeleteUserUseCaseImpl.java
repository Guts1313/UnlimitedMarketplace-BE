package unlimitedmarketplace.business.impl;

import org.springframework.stereotype.Service;
import unlimitedmarketplace.business.DeleteUserUseCase;
import unlimitedmarketplace.business.exceptions.UserNotFoundException;
import unlimitedmarketplace.persistence.UserRepository;

@Service
public class DeleteUserUseCaseImpl implements DeleteUserUseCase {
    private final UserRepository userRepository;

    public DeleteUserUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException();
        }
        userRepository.deleteById(id);
    }
}
