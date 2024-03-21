package semester3_angel_unlimitedmarketplace.business.customexceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class UserNotFoundException extends ResponseStatusException {

    public UserNotFoundException(final Long id) {
        super(HttpStatus.NOT_FOUND,"USER_NOT_FOUND");
    }
}
