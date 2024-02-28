package jokes_application.persistence.impl;

import jokes_application.domain.Joke;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import jokes_application.persistence.JokesRepository;

@Repository
@Primary
public class JokesRepositoryImpl implements JokesRepository
{
    private final RestTemplate restTemplate;
    private final String apiUrl = "https://v2.jokeapi.dev/joke/Any";

    public JokesRepositoryImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Joke fetchRandomJoke() {
        ResponseEntity<Joke> response = restTemplate.getForEntity(apiUrl, Joke.class);
        return response.getBody();
    }

    public Joke fetchJokeByCategory(String category) {
        String urlWithCategory = apiUrl.replace("/Any", "/" + category); // Correct approach to replace 'Any' with the actual category
        System.out.println("Category when fetching from repo: " + category);
        ResponseEntity<Joke> response = restTemplate.getForEntity(urlWithCategory, Joke.class);
        return response.getBody();
    }

}
