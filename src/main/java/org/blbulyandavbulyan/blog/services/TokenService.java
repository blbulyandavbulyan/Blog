package org.blbulyandavbulyan.blog.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.configs.JwtConfigurationProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

/**
 * Данный класс предназначен для удобной работы с jwt токенами
 */
@Component
@RequiredArgsConstructor
public class TokenService {
    /**
     * Время жизни jwt токена
     */
    private final JwtConfigurationProperties jwtConfigurationProperties;
    /**
     * Ключ для подписи jwt токена
     */
    private Key secretKey;
    /**
     * Парсер, для парсинга jwt токена
     */
    private JwtParser parser;

    /**
     * Метод инициализации нашего бина
     */
    @PostConstruct
    private void init(){
        //задаём ключ подписи
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256); //or HS384 or HS512
        //создаём парсер
        parser = Jwts.parserBuilder().setSigningKey(secretKey).build();
    }

    /**
     * Метод генерирует jwt токен
     * @param name имя пользователя
     * @param authorities права пользователя
     * @return полученный jwt токен
     */
    public String generateToken(String name, Collection<? extends GrantedAuthority> authorities){
        Map<String, Object> claims = new HashMap<>();
        List<String> rolesList = authorities.stream().map(GrantedAuthority::getAuthority).toList();
        claims.put("roles", rolesList);//добавляем роли
        Date issuedDate = new Date();//время создания токена
        Date expiredDate = new Date(issuedDate.getTime() + jwtConfigurationProperties.getLifetime().toMillis());//время истечения токена
        return Jwts.builder().setClaims(claims)
                .setSubject(name)
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith(secretKey).compact();
    }

    public Claims getAllClaimsFromToken(String token){
        return parser.parseClaimsJws(token).getBody();
    }

    /**
     * Получаем имя пользователя из jwt токена
     * @param token jwt токен
     * @return имя пользователя, которое было в jwt токене
     */
    public String getUserName(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    /**
     * Получаем список ролей из jwt токена
     * @param token jwt токен, из которого будут получены роли
     * @return список ролей, которые были в jwt токене
     */
    public List<String> getRoles(String token) {
        return getAllClaimsFromToken(token).get("roles", List.class);
    }
}
