package semester3_angel_unlimitedmarketplace.business;

import semester3_angel_unlimitedmarketplace.domain.GetAllUsersRequest;
import semester3_angel_unlimitedmarketplace.domain.GetAllUsersResponse;

import java.util.List;

public interface GetUsersUseCase {
    GetAllUsersResponse getAllUsers(GetAllUsersRequest request);
}
