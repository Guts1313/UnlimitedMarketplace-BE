package semester3_angel_unlimitedmarketplace.business.impl;

import org.springframework.stereotype.Service;
import semester3_angel_unlimitedmarketplace.business.DeleteUserUseCase;
import semester3_angel_unlimitedmarketplace.business.customexceptions.UserNotFoundException;
import semester3_angel_unlimitedmarketplace.persistence.UserRepository;

@Service
public class DeleteUserUseCaseImpl implements DeleteUserUseCase {
    private final UserRepository userRepository;

    public DeleteUserUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}
