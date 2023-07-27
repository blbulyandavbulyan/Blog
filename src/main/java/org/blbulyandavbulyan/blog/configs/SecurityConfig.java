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
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Конфигурация Spring Security
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig{
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
    public DaoAuthenticationProvider daoAuthenticationProvider(@Autowired UserService userService, @Autowired PasswordEncoder passwordEncoder){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userService);
        return daoAuthenticationProvider;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors().disable()
                .antMatcher("/api/**")
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/v1/articles/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/v1/articles/**").authenticated()
                .antMatchers(HttpMethod.PUT, "/api/v1/articles/**").authenticated()
                .antMatchers(HttpMethod.POST, "/api/v1/comments/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/v1/comments/**").authenticated()
                .antMatchers(HttpMethod.PUT, "/api/v1/comments/**").authenticated()
                .antMatchers("/api/v1/users/register").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/users/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/v1/users/**").authenticated()
                .antMatchers(HttpMethod.PUT, "/api/v1/users/**").authenticated()
                .antMatchers(HttpMethod.GET, "/api/v1/users/info").authenticated()
                .anyRequest().permitAll()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and().addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
