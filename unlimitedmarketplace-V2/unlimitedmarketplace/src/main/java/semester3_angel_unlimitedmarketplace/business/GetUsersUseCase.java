package semester3_angel_unlimitedmarketplace.business;

import semester3_angel_unlimitedmarketplace.domain.GetAllUsersRequest;
import semester3_angel_unlimitedmarketplace.domain.GetAllUsersResponse;


public interface GetUsersUseCase {
    GetAllUsersResponse getAllUsers(GetAllUsersRequest request);
}
