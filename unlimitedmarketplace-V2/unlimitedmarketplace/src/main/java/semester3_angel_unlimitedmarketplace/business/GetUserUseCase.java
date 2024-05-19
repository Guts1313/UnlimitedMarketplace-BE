package semester3_angel_unlimitedmarketplace.business;

import semester3_angel_unlimitedmarketplace.domain.GetUserResponse;


public interface GetUserUseCase {

    GetUserResponse getUserById(Long id);
}
