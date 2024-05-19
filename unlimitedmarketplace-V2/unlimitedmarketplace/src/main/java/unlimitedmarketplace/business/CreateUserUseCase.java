package unlimitedmarketplace.business;


import unlimitedmarketplace.domain.CreateUserRequest;
import unlimitedmarketplace.domain.CreateUserResponse;

public interface CreateUserUseCase {

    CreateUserResponse saveUser(CreateUserRequest request);
}
