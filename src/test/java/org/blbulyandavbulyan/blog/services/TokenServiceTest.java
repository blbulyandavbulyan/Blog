package org.blbulyandavbulyan.blog.services;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.blbulyandavbulyan.blog.configs.JwtConfigurationProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
    @Mock
    private JwtConfigurationProperties jwtConfigurationProperties;

    @Test
    void generateToken() {
        Duration expectedLifetime = Duration.ofMinutes(10);
        when(jwtConfigurationProperties.getLifetime()).thenReturn(expectedLifetime);
        try (MockedStatic<Jwts> jwtsMockedStatic = Mockito.mockStatic(Jwts.class); MockedStatic<Keys> keysMockedStatic = Mockito.mockStatic(Keys.class)) {
            JwtParserBuilder mockParserBuilder = mock(JwtParserBuilder.class);
            jwtsMockedStatic.when(Jwts::parserBuilder).thenReturn(mockParserBuilder);
            Key keyMock = Mockito.mock(SecretKey.class);
            when(mockParserBuilder.setSigningKey(keyMock)).thenAnswer(InvocationOnMock::getMock);
            keysMockedStatic.when(()->Keys.secretKeyFor(SignatureAlgorithm.HS256)).thenReturn(keyMock);
            var underTest = new TokenService(jwtConfigurationProperties);
            String expectedName = "testuser";
            var expectedRoles = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_COMMENTER"));
            String expectedJwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
            JwtBuilder mockJwtBuilder = Mockito.mock(JwtBuilder.class);
            when(mockJwtBuilder.setClaims(any(Map.class))).thenAnswer(InvocationOnMock::getMock);
            when(mockJwtBuilder.setSubject(anyString())).thenAnswer(InvocationOnMock::getMock);
            when(mockJwtBuilder.setIssuedAt(any(Date.class))).thenAnswer(InvocationOnMock::getMock);
            when(mockJwtBuilder.setExpiration(any(Date.class))).thenAnswer(InvocationOnMock::getMock);
            when(mockJwtBuilder.signWith(any(SecretKey.class))).thenAnswer(InvocationOnMock::getMock);
            when(mockJwtBuilder.compact()).thenReturn(expectedJwtToken);
            jwtsMockedStatic.when(Jwts::builder).thenReturn(mockJwtBuilder);
            String actualToken =  underTest.generateToken(expectedName, expectedRoles);
            assertSame(expectedJwtToken, actualToken);
            HashMap<String, Object> expectedClaims = new HashMap<>();
            expectedClaims.put("roles", expectedRoles.stream().map(SimpleGrantedAuthority::getAuthority).toList());
            verify(mockJwtBuilder, times(1)).setClaims(expectedClaims);
            verify(mockJwtBuilder, times(1)).setSubject(expectedName);
            ArgumentCaptor<Date> issuedAtArgumentCaptor = ArgumentCaptor.forClass(Date.class);
            ArgumentCaptor<Date> expirationArgumentCaptor = ArgumentCaptor.forClass(Date.class);
            verify(mockJwtBuilder, times(1)).setIssuedAt(issuedAtArgumentCaptor.capture());
            verify(mockJwtBuilder, times(1)).setExpiration(expirationArgumentCaptor.capture());
            Date issuedAt = issuedAtArgumentCaptor.getValue();
            Date expiration = expirationArgumentCaptor.getValue();
            assertTrue(issuedAt.before(new Date()));
            assertEquals(expiration.getTime() - issuedAt.getTime(), expectedLifetime.toMillis());
            verify(mockJwtBuilder, times(1)).signWith(keyMock);
        }
    }
}