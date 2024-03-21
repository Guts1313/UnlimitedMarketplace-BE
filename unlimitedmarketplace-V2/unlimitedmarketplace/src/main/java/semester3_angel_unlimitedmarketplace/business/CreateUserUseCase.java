package semester3_angel_unlimitedmarketplace.business;


import semester3_angel_unlimitedmarketplace.domain.CreateUserRequest;
import semester3_angel_unlimitedmarketplace.domain.CreateUserResponse;

public interface CreateUserUseCase {

    CreateUserResponse saveUser(CreateUserRequest request);
}
