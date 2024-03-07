package semester3_angel_unlimitedmarketplace.business.customexceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class DuplicateUsernameException extends ResponseStatusException {
    public DuplicateUsernameException() {
        super(HttpStatus.BAD_REQUEST,"DUPLICATE_USERNAME_EXCEPTION");
    }
}
