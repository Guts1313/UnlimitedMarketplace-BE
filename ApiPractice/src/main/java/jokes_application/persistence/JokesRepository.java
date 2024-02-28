package jokes_application.persistence;

import jokes_application.domain.Joke;
import org.springframework.stereotype.Repository;

@Repository
public interface JokesRepository {

    Joke fetchRandomJoke();
    Joke fetchJokeByCategory(String category);
}
