package jokes_application.configuration;//package jokes_application.configuration;

import jokes_application.business.JokeService;
import jokes_application.business.JokeServiceImpl;
import jokes_application.persistence.JokesRepository;
import jokes_application.persistence.impl.JokesRepositoryImpl;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {



    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }
    @Bean
    public JokesRepository newJokeRepo(RestTemplate restTemplate){
        return new JokesRepositoryImpl(restTemplate);
    }
    @Bean
    public JokeService newJokeService(JokesRepository jokesRepository){
        return new JokeServiceImpl(jokesRepository);
    }


}

