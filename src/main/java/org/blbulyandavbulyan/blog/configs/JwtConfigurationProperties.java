package org.blbulyandavbulyan.blog.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "jwt")
@Component
public class JwtConfigurationProperties {
    private Duration mainTokenLifetime;
    private String mainKeyBytes;
    private Duration tfaSecondStepLifetime;
    private String tfaSecondStepKeyBytes;
}
