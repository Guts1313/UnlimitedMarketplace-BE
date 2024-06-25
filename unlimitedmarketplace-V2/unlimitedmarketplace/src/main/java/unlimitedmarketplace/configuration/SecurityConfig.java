package unlimitedmarketplace.configuration;


import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import unlimitedmarketplace.business.impl.UserDetailsServiceImpl;

import unlimitedmarketplace.persistence.repositories.UserRepository;

import javax.crypto.SecretKey;

import java.util.Arrays;

import java.util.List;
import java.util.Random;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableMethodSecurity(jsr250Enabled = true)
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepository;
    private static final String LOGIN = "/login";  // Compliant
    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("https://sem3-fe-frontend-myvoxyxc3a-lz.a.run.app")); // Use patterns for flexibility
        configuration.setAllowedOriginPatterns(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        // If you are using credentials (cookies, authentication), you must specify origins, not use '*'
        configuration.setAllowCredentials(true); // This should be set based on your specific needs
        configuration.setMaxAge(3600L); // 1 hour
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.cors(withDefaults());
        http.requiresChannel(c -> c.requestMatchers("/actuator/**").requiresInsecure());
        http.exceptionHandling(exceptions ->exceptions.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
        http.sessionManagement(sessionAuthenticationStrategy ->
                sessionAuthenticationStrategy.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(request -> {
            request.requestMatchers(
                            "/api/v*/registration/**",
                            "/register*",
                            LOGIN,
                            ("/unlimitedmarketplace/**"),
                            "/actuator/**","unlimitedmarketplace/auth/login").permitAll()
                    .requestMatchers("/unlimitedmarketplace/products/").authenticated()
                    .requestMatchers("/websocket-sockjs-stomp/**").permitAll()
            ;

            request.anyRequest().permitAll();

        });
        return http.build();

    }

    @Bean
    public SecretKey jwtSecretKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS256); // Secure key generation
    }
    @Bean
    public Random random() {
        return new Random();
    }
    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl(userRepository);

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


}






