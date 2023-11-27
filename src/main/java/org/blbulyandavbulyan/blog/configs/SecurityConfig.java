package org.blbulyandavbulyan.blog.configs;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

/**
 * Конфигурация Spring Security
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    /**
     * Фильтр для отслеживания jwt токенов
     */
    private final JwtRequestFilter jwtRequestFilter;

    /**
     * @return данный бин будет использоваться для хэширования паролей
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(@Autowired UserService userService, @Autowired PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userService);
        return daoAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .securityMatcher("/api/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/v1/articles/**")).authenticated()
                        .requestMatchers(antMatcher(HttpMethod.DELETE, "/api/v1/articles/**")).authenticated()
                        .requestMatchers(antMatcher(HttpMethod.PUT, "/api/v1/articles/**")).authenticated()
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/v1/comments/**")).authenticated()
                        .requestMatchers(antMatcher(HttpMethod.DELETE, "/api/v1/comments/**")).authenticated()
                        .requestMatchers(antMatcher(HttpMethod.PUT, "/api/v1/comments/**")).authenticated()
                        .requestMatchers(antMatcher("/api/v1/users/register")).permitAll()
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/v1/users/**")).authenticated()
                        .requestMatchers(antMatcher(HttpMethod.DELETE, "/api/v1/users/**")).authenticated()
                        .requestMatchers(antMatcher(HttpMethod.PUT, "/api/v1/users/**")).authenticated()
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/v1/users/info")).authenticated()
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/v1/reactions/**")).authenticated()
                        .requestMatchers(antMatcher("/api/v1/reactions/article/{articleId}")).authenticated()
                        .requestMatchers(antMatcher("/api/v1/reactions/comment/{commentId}")).authenticated()
                        .requestMatchers(antMatcher(HttpMethod.DELETE, "/api/v1/reactions/**")).authenticated()
                        .anyRequest().permitAll()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
