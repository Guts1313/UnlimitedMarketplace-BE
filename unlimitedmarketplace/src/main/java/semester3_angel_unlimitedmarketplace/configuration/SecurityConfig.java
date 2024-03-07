package semester3_angel_unlimitedmarketplace.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig  {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Apply CSRF configuration
                .csrf(AbstractHttpConfigurer::disable)
                // Apply URL-based authorization
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/unlimitedmarketplace/{id}","/unlimitedmarketplace", "/public/**").permitAll()  // Permit all for specified paths
                        .anyRequest().authenticated()  // All other requests need to be authenticated
                )
                // Apply HTTP Basic authentication
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }


}
