package unlimitedmarketplace.business.interfaces;

import unlimitedmarketplace.domain.UpdateUserPasswordRequest;

public interface UpdateUserPasswordUseCase {
    void updatePassword(UpdateUserPasswordRequest request);
}
