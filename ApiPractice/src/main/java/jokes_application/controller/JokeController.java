package jokes_application.controller;

import jokes_application.business.JokeService;
import jokes_application.business.JokeServiceImpl;
import jokes_application.domain.JokeRequest;
import jokes_application.domain.JokeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class JokeController {

    private final JokeService jokeService;

    public JokeController(JokeService jokeService) {
        this.jokeService = jokeService;
    }

    // Endpoint to get a joke by category
    @GetMapping("/joke/{category}")
    public ResponseEntity<Object> getJokeByCategory(@PathVariable(value = "category") String category) {
        return getJokeResponse(category);
    }

    // Endpoint to get a random joke
    @GetMapping("/joke")
    public ResponseEntity<Object> getRandomJoke() {
        return getJokeResponse(null); // No category specified
    }

    // Common method to process joke fetching
    private ResponseEntity<Object> getJokeResponse(String category) {
        JokeRequest request = new JokeRequest();
        request.setCategory(category); // This will be null for random jokes

        JokeResponse jokeResponse = jokeService.getJokeResponse(request);

        if (jokeResponse.getJoke() != null) {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("setup", jokeResponse.getJoke().getSetup());
            response.put("delivery", jokeResponse.getJoke().getDelivery());
            response.put("category", jokeResponse.getJoke().getCategory());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Joke not found"));
        }
    }
}