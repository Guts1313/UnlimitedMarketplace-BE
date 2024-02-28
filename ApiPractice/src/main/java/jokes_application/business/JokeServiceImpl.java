package jokes_application.business;

import jokes_application.domain.Joke;
import jokes_application.domain.JokeRequest;
import jokes_application.domain.JokeResponse;
import org.springframework.stereotype.Service;
import jokes_application.persistence.JokesRepository;

public class JokeServiceImpl implements JokeService {
    private final JokesRepository repository;

    public JokeServiceImpl(JokesRepository jokesRepository) {
         repository = jokesRepository;
    }

    @Override
    public JokeResponse getJokeResponse(JokeRequest request) {
        System.out.println("Request's category: " + request.getCategory());
        JokeResponse response = new JokeResponse(); // Assuming JokeResponse has a no-arg constructor
        if (request.getCategory() == null || request.getCategory().isEmpty()) {
            // Fetching random joke if category is not specified
            System.out.println(request.getCategory());
            Joke returnedRandomJoke = repository.fetchRandomJoke();
            response.setJoke(returnedRandomJoke);
        } else {
            // Fetching joke from specific category

            Joke returnedCatJoke = repository.fetchJokeByCategory(request.getCategory());
            System.out.println("Category when fetched:" + returnedCatJoke.getCategory());
            response.setJoke(returnedCatJoke);
        }
        return response;
    }
}
