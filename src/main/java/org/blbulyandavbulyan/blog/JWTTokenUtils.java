package org.blbulyandavbulyan.blog;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JWTTokenUtils {
    @Value("${jwt.lifetime}")
    private Duration jwtLifeTime;
    private Key secretKey;
    private JwtParser parser;

    @PostConstruct
    private void init(){
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256); //or HS384 or HS512
        parser = Jwts.parserBuilder().setSigningKey(secretKey).build();
    }
    public String generateToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
        List<String> rolesList = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        claims.put("roles", rolesList);
        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + jwtLifeTime.toMillis());
        return Jwts.builder().setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith(secretKey).compact();
    }
    public Claims getAllClaimsFromToken(String token){
        return parser.parseClaimsJwt(token).getBody();
    }
}
