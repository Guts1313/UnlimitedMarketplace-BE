package jokes_application.domain;



import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class JokeResponse {
    private boolean error;
    private Joke joke;
    private List<String> jokes;
    private String category;


}
