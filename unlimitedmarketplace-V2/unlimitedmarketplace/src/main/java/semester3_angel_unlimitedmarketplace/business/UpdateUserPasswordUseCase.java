package semester3_angel_unlimitedmarketplace.business;

import semester3_angel_unlimitedmarketplace.domain.UpdateUserPasswordRequest;

public interface UpdateUserPasswordUseCase {
    void updatePassword(UpdateUserPasswordRequest request);
}
