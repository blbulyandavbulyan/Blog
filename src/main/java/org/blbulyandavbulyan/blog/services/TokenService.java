package org.blbulyandavbulyan.blog.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.blbulyandavbulyan.blog.exceptions.TokenServiceParseException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.*;

/**
 * Данный класс предназначен для удобной работы с jwt токенами
 */
@Component
public class TokenService {
    /**
     * Время жизни jwt токена
     */
    private final Duration lifetime;
    /**
     * Ключ для подписи jwt токена
     */
    private final Key secretKey;
    /**
     * Парсер, для парсинга jwt токена
     */
    private final JwtParser parser;

    /**
     * @param lifetime время жизни токена
     * @param secretKey ключ подписи
     */
    public TokenService(Duration lifetime, Key secretKey) {
        this.lifetime = lifetime;
        this.secretKey = secretKey;
        parser = Jwts.parserBuilder().setSigningKey(secretKey).build();
    }

    /**
     * Генерирует токен с заданными claims
     * @param name имя пользователя(subject)
     * @param claims claims, которые нужно подложить в токен
     * @return сгенерированный токен
     */
    public String generateToken(String name, Map<String, Object> claims) {
        Date issuedDate = new Date();//время создания токена
        Date expiredDate = new Date(issuedDate.getTime() + lifetime.toMillis());//время истечения токена
        return Jwts.builder().setClaims(claims)
                .setSubject(name)
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith(secretKey).compact();
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
        return generateToken(name, claims);
    }

    /**
     * Генерирует токен с пустыми claims
     * @param name имя пользователя
     * @return сгенерированный токен
     * @throws TokenServiceParseException если не удалось обработать токен
     */
    public String generateToken(String name) {
        return generateToken(name, Map.of());
    }
    private Claims getAllClaimsFromToken(String token){
        try {
            return parser.parseClaimsJws(token).getBody();
        } catch (Exception e) {
            throw new TokenServiceParseException("Error during parsing token", e);
        }
    }

    /**
     * Получаем имя пользователя из jwt токена
     * @param token jwt токен
     * @return имя пользователя, которое было в jwt токене
     * @throws TokenServiceParseException если не удалось обработать токен
     */
    public String getUserName(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    /**
     * Получаем список ролей из jwt токена
     * @param token jwt токен, из которого будут получены роли
     * @return список ролей, которые были в jwt токене
     * @throws TokenServiceParseException если не удалось обработать токен
     */
    public List<String> getRoles(String token) {
        return getAllClaimsFromToken(token).get("roles", List.class);
    }
}
