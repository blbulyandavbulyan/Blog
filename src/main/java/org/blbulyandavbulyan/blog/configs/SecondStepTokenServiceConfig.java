package org.blbulyandavbulyan.blog.configs;

import org.blbulyandavbulyan.blog.services.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class SecondStepTokenServiceConfig {
    @Bean
    public TokenService secondStepTokenService() {
        JwtConfigurationProperties jwtConfigurationProperties = new JwtConfigurationProperties();
        jwtConfigurationProperties.setLifetime(Duration.ofMinutes(5));
        return new TokenService(jwtConfigurationProperties);
    }
}
