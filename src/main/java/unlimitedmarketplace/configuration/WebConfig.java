package unlimitedmarketplace.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("http://localhost:3000")
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .allowedHeaders("Authorization", "Content-Type")
//                .allowCredentials(true);
        registry.addMapping("/**")
                .allowedOrigins("https://sem3-fe-frontend-myvoxyxc3a-lz.a.run.app")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type")
                .allowCredentials(true);

    }
}
