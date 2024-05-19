package unlimitedmarketplace.business;

import unlimitedmarketplace.domain.UpdateUserPasswordRequest;

public interface UpdateUserPasswordUseCase {
    void updatePassword(UpdateUserPasswordRequest request);
}
