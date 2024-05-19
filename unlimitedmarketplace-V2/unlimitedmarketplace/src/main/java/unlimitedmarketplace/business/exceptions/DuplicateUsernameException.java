package unlimitedmarketplace.business.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class DuplicateUsernameException extends ResponseStatusException {
    public DuplicateUsernameException(HttpStatusCode statusCode) {
        super(HttpStatus.BAD_REQUEST,"DUPLICATE_USERNAME_EXCEPTION");
    }
}
