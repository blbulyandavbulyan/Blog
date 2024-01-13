package org.blbulyandavbulyan.blog.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.blbulyandavbulyan.blog.configs.JwtConfigurationProperties;
import org.blbulyandavbulyan.blog.exceptions.TokenServiceParseException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

/**
 * Данный класс предназначен для удобной работы с jwt токенами
 */
@Component
public class TokenService {
    /**
     * Время жизни jwt токена
     */
    private final JwtConfigurationProperties jwtConfigurationProperties;
    /**
     * Ключ для подписи jwt токена
     */
    private final Key secretKey;
    /**
     * Парсер, для парсинга jwt токена
     */
    private final JwtParser parser;

    /**
     * Создаёт экземпляр сервиса
     * @param jwtConfigurationProperties класс, содержащий конфигурационные свойства для jwt
     */
    public TokenService(JwtConfigurationProperties jwtConfigurationProperties) {
        this.jwtConfigurationProperties = jwtConfigurationProperties;
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

    /**
     * Генерирует токен без ролей
     * @param name имя пользователя
     * @return сгенерированный токен
     * @throws TokenServiceParseException если не удалось обработать токен
     */
    public String generateToken(String name) {
        return generateToken(name, List.of());
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
