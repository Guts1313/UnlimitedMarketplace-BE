package unlimitedmarketplace.business.impl;

import org.springframework.stereotype.Service;
import unlimitedmarketplace.business.UpdateUserPasswordUseCase;
import unlimitedmarketplace.domain.UpdateUserPasswordRequest;
import unlimitedmarketplace.persistence.UserRepository;
import unlimitedmarketplace.persistence.entity.UserEntity;

import java.util.Optional;

@Service
public class UpdateUserPasswordUseCaseImpl implements UpdateUserPasswordUseCase {
    private final UserRepository userRepository;

    public UpdateUserPasswordUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        user.setPasswordHash(request.getNewPassword());
        userRepository.save(user);
    }

}
