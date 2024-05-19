package semester3_angel_unlimitedmarketplace.configuration;


import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import semester3_angel_unlimitedmarketplace.business.impl.UserDetailsServiceImpl;

import semester3_angel_unlimitedmarketplace.persistence.UserRepository;

import javax.crypto.SecretKey;

import java.util.Arrays;

import java.util.List;

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
        configuration.setAllowedOriginPatterns(List.of("http://localhost:3000")); // Use patterns for flexibility
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        // If you are using credentials (cookies, authentication), you must specify origins, not use '*'
        configuration.setAllowCredentials(true); // This should be set based on your specific needs
        configuration.setMaxAge(3600L); // 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

//    public SecurityFilterChain filterChain(HttpSecurity httpSecurity,
//                                           AuthenticationEntryPoint authenticationEntryPoint,
//                                           AuthenticationRequestFilter authenticationRequestFilter) throws Exception {
//        httpSecurity
//                .csrf(AbstractHttpConfigurer::disable)
//                .formLogin(AbstractHttpConfigurer::disable)
//                .sessionManagement(configurer ->
//                        configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(registry ->
//                        registry.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()                 // CORS pre-flight requests should be public
//                                .requestMatchers(HttpMethod.POST, "/students", "/tokens").permitAll() // Creating a student and login are public
////                                .requestMatchers(SWAGGER_UI_RESOURCES).permitAll()                        // Swagger is also public (In "real life" it would only be public in non-production environments)
//                                .anyRequest().authenticated()                                             // Everything else --> authentication required, which is Spring security's default behaviour
//                )
//                .exceptionHandling(configure -> configure.authenticationEntryPoint(authenticationEntryPoint))
//                .addFilterBefore(authenticationRequestFilter, UsernamePasswordAuthenticationFilter.class);
//        return httpSecurity.build();
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(withDefaults());
        http.requiresChannel(c -> c.requestMatchers("/actuator/**").requiresInsecure());
        http.authorizeHttpRequests(request -> {
            request.requestMatchers(
                            "/api/v*/registration/**",
                            "/register*",
                            LOGIN,
                            ("/unlimitedmarketplace/**"),
                            "/actuator/**").permitAll()
                    .requestMatchers("/unlimitedmarketplace/products/").authenticated()
                    .requestMatchers("/websocket-sockjs-stomp/**").permitAll()  // Allow all WebSocket connection requests
            ;

            request.anyRequest().permitAll();

        });
        http.formLogin(fL -> fL.loginPage(LOGIN)
                .usernameParameter("email").permitAll()
                .defaultSuccessUrl("/", true)
                .failureUrl("/login"));
        http.logout(logOut -> logOut.logoutUrl("/logout")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "Idea-2e8e7cee")
                .logoutSuccessUrl(LOGIN));

        return http.build();

    }

    @Bean
    public SecretKey jwtSecretKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS256); // Secure key generation
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






