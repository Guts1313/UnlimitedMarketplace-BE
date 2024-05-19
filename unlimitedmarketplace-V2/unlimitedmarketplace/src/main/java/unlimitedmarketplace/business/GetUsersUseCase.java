package unlimitedmarketplace.business;

import unlimitedmarketplace.domain.GetAllUsersRequest;
import unlimitedmarketplace.domain.GetAllUsersResponse;


public interface GetUsersUseCase {
    GetAllUsersResponse getAllUsers(GetAllUsersRequest request);
}
