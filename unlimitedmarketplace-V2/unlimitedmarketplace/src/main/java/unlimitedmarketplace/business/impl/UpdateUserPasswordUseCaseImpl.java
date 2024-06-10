package unlimitedmarketplace.business.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import unlimitedmarketplace.business.UpdateUserPasswordUseCase;
import unlimitedmarketplace.business.exceptions.InvalidUserException;
import unlimitedmarketplace.domain.UpdateUserPasswordRequest;
import unlimitedmarketplace.persistence.UserRepository;
import unlimitedmarketplace.persistence.entity.UserEntity;

import java.util.Optional;

@Service
public class UpdateUserPasswordUseCaseImpl implements UpdateUserPasswordUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UpdateUserPasswordUseCaseImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void updatePassword(UpdateUserPasswordRequest request) {
        Optional<UserEntity> userOptional = userRepository.findById(request.getId());
        if (userOptional.isEmpty()){
            throw new InvalidUserException();
        }
        UserEntity user = userOptional.get();
        updatePasswordHash(request,user);

    }
    private void updatePasswordHash(UpdateUserPasswordRequest request, UserEntity user){
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

}
