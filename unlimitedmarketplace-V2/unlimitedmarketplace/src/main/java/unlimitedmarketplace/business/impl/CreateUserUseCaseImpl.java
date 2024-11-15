package unlimitedmarketplace.business.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import unlimitedmarketplace.business.interfaces.CreateUserUseCase;
import unlimitedmarketplace.business.exceptions.DuplicateEmailException;
import unlimitedmarketplace.business.exceptions.DuplicateUsernameException;
import unlimitedmarketplace.persistence.repositories.UserRepository;
import unlimitedmarketplace.domain.CreateUserRequest;
import unlimitedmarketplace.domain.CreateUserResponse;
import unlimitedmarketplace.persistence.entity.UserEntity;

@Service
public class CreateUserUseCaseImpl implements CreateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public CreateUserUseCaseImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public CreateUserResponse saveUser(CreateUserRequest request) {
        // Check if the username or email already exists
        if (userRepository.findByUserName(request.getUserName()).isPresent()) {
            throw new DuplicateUsernameException(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()));
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateEmailException();
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(request.getUserName());
        userEntity.setPasswordHash(passwordEncoder.encode(request.getPasswordHash())); // Hashing the password
        userEntity.setEmail(request.getEmail());
        userEntity.setUserRole(request.getRole());
        UserEntity savedUser = userRepository.save(userEntity);

        return new CreateUserResponse(savedUser.getId(), savedUser.getUserName(), savedUser.getEmail(),savedUser.getUserRole());
    }

}


