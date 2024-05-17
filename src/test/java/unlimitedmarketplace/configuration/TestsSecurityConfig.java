package unlimitedmarketplace.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Random;

@Configuration
@EnableWebSecurity
@Profile("test")
 public class TestsSecurityConfig {

    @Bean
    public SecurityFilterChain disableSecurity(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v*/registration/**", "/register*", "/login", "/actuator/**").permitAll()
                        .requestMatchers("/unlimitedmarketplace/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
