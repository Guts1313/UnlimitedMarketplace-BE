package unlimitedmarketplace.business.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserNotFoundException extends ResponseStatusException {

    public UserNotFoundException(final Long id) {
        super(HttpStatus.NOT_FOUND,"USER_NOT_FOUND");
    }
}
