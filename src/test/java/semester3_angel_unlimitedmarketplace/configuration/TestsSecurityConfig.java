package semester3_angel_unlimitedmarketplace.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("test") // Ensure this configuration is only active when the "test" profile is used

public class TestsSecurityConfig {

    @Bean
    public SecurityFilterChain disableSecurity(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz.anyRequest().permitAll()) // Permit all requests
                .csrf(AbstractHttpConfigurer::disable); // Disable CSRF protection
        System.out.println("Loading Test Security Configuration");

        return http.build();
    }

}
