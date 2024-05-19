package unlimitedmarketplace.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("test") // Ensures this configuration is only active when the "test" profile is used
public class TestsSecurityConfig {

    @Bean
    public SecurityFilterChain disableSecurity(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disabling CSRF as per your setup
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v*/registration/**", "/register*", "/login", "/actuator/**").permitAll()  // Allowing unauthenticated access to these endpoints
                        .requestMatchers("/unlimitedmarketplace/**").hasRole("ADMIN")  // Restricting this endpoint to ADMIN only
                        .anyRequest().authenticated()  // All other requests require authentication
                );

        return http.build();
    }
}
