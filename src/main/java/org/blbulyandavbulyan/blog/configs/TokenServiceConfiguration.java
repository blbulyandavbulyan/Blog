package org.blbulyandavbulyan.blog.configs;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.services.KeyProviderService;
import org.blbulyandavbulyan.blog.services.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Configuration
@RequiredArgsConstructor
public class TokenServiceConfiguration {
    private final JwtConfigurationProperties jwtConfigurationProperties;
    private final KeyProviderService keyProviderService;

    @Bean
    public TokenService secondStepTokenService() {
        Key key = keyProviderService.getFromBytesOrElseRandom(jwtConfigurationProperties.getTfaSecondStepKeyBytes().getBytes(StandardCharsets.UTF_8));
        return new TokenService(jwtConfigurationProperties.getTfaSecondStepLifetime(), key);
    }

    @Bean
    public TokenService tokenService() {
        Key key = keyProviderService.getFromBytesOrElseRandom(jwtConfigurationProperties.getMainKeyBytes().getBytes(StandardCharsets.UTF_8));
        return new TokenService(jwtConfigurationProperties.getMainTokenLifetime(), key);
    }
}
