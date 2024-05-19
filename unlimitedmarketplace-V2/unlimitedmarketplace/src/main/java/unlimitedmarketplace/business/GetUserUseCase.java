package unlimitedmarketplace.business;

import unlimitedmarketplace.domain.GetUserResponse;


public interface GetUserUseCase {

    GetUserResponse getUserById(Long id);
}
