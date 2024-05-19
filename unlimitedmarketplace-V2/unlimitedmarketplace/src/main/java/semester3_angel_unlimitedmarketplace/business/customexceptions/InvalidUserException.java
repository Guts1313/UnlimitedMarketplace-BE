package semester3_angel_unlimitedmarketplace.business.customexceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidUserException extends ResponseStatusException {
    public InvalidUserException() {
        super(HttpStatus.BAD_REQUEST,"INVALID_USER_EXCEPTION");
    }
}
