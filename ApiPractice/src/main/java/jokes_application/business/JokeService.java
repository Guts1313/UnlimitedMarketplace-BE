package jokes_application.business;

import jokes_application.domain.JokeRequest;
import jokes_application.domain.JokeResponse;
import org.springframework.stereotype.Component;


public interface JokeService {
    JokeResponse getJokeResponse(JokeRequest request);
}
