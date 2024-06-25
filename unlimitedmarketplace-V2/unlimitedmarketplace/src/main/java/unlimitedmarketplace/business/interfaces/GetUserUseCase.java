package unlimitedmarketplace.business.interfaces;

import unlimitedmarketplace.domain.GetUserResponse;


public interface GetUserUseCase {

    GetUserResponse getUserById(Long id);
}
