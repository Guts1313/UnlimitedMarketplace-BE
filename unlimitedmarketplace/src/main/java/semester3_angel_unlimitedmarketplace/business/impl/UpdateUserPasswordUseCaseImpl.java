package semester3_angel_unlimitedmarketplace.business.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import semester3_angel_unlimitedmarketplace.business.UpdateUserPasswordUseCase;
import semester3_angel_unlimitedmarketplace.domain.UpdateUserPasswordRequest;
import semester3_angel_unlimitedmarketplace.persistence.UserRepository;
import semester3_angel_unlimitedmarketplace.persistence.entity.UserEntity;

import java.beans.Encoder;
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
            throw new RuntimeException("Null userOptional");
        }
        UserEntity user = userOptional.get();
        updatePasswordHash(request,user);

    }
    private void updatePasswordHash(UpdateUserPasswordRequest request, UserEntity user){
        UserEntity userEntity = userRepository.getReferenceById(request.getId());
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

}
