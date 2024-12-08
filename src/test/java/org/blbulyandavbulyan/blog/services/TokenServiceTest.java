package org.blbulyandavbulyan.blog.services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Test
    void generateToken() {
        Duration expectedLifetime = Duration.ofMinutes(10);
        try (MockedStatic<Jwts> jwtsMockedStatic = Mockito.mockStatic(Jwts.class);
             MockedStatic<Keys> keysMockedStatic = Mockito.mockStatic(Keys.class)) {
            String expectedJwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
            String expectedName = "testuser";
            var expectedRoles = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_COMMENTER"));
            Key keyMock = mock(SecretKey.class);
            JwtBuilder mockJwtBuilder = mock(JwtBuilder.class);
            JwtParserBuilder mockParserBuilder = mock(JwtParserBuilder.class);
            jwtsMockedStatic.when(Jwts::parserBuilder).thenReturn(mockParserBuilder);
            when(mockParserBuilder.setSigningKey(keyMock)).thenAnswer(InvocationOnMock::getMock);
            keysMockedStatic.when(() -> Keys.secretKeyFor(SignatureAlgorithm.HS256)).thenReturn(keyMock);
            when(mockJwtBuilder.setClaims(anyMap())).thenAnswer(InvocationOnMock::getMock);
            when(mockJwtBuilder.setSubject(anyString())).thenAnswer(InvocationOnMock::getMock);
            when(mockJwtBuilder.setIssuedAt(any(Date.class))).thenAnswer(InvocationOnMock::getMock);
            when(mockJwtBuilder.setExpiration(any(Date.class))).thenAnswer(InvocationOnMock::getMock);
            when(mockJwtBuilder.signWith(any(SecretKey.class))).thenAnswer(InvocationOnMock::getMock);
            when(mockJwtBuilder.compact()).thenReturn(expectedJwtToken);
            jwtsMockedStatic.when(Jwts::builder).thenReturn(mockJwtBuilder);
            var underTest = new TokenService(expectedLifetime, keyMock);
            String actualToken = assertDoesNotThrow(()-> underTest.generateToken(expectedName, expectedRoles));
            assertSame(expectedJwtToken, actualToken);
            HashMap<String, Object> expectedClaims = new HashMap<>();
            expectedClaims.put("roles", expectedRoles.stream().map(SimpleGrantedAuthority::getAuthority).toList());
            ArgumentCaptor<Date> issuedAtArgumentCaptor = ArgumentCaptor.forClass(Date.class);
            ArgumentCaptor<Date> expirationArgumentCaptor = ArgumentCaptor.forClass(Date.class);
            verify(mockJwtBuilder, times(1)).setIssuedAt(issuedAtArgumentCaptor.capture());
            verify(mockJwtBuilder, times(1)).setExpiration(expirationArgumentCaptor.capture());
            Date issuedAt = issuedAtArgumentCaptor.getValue();
            Date expiration = expirationArgumentCaptor.getValue();
            assertTrue(issuedAt.before(new Date()));
            assertEquals(expiration.getTime() - issuedAt.getTime(), expectedLifetime.toMillis());
            verify(mockJwtBuilder, times(1)).setClaims(expectedClaims);
            verify(mockJwtBuilder, times(1)).setSubject(expectedName);
            verify(mockJwtBuilder, times(1)).signWith(keyMock);
        }
    }

    private void executeWithMockClaims(BiConsumer<Claims, TokenService> testLogic, String token) {
        try (MockedStatic<Jwts> jwtsMockedStatic = Mockito.mockStatic(Jwts.class)) {
            JwtParserBuilder mockParserBuilder = mock(JwtParserBuilder.class);
            jwtsMockedStatic.when(Jwts::parserBuilder).thenReturn(mockParserBuilder);
            Key keyMock = mock(SecretKey.class);
            when(mockParserBuilder.setSigningKey(keyMock)).thenAnswer(InvocationOnMock::getMock);
            JwtParser mockJwtParser = mock(JwtParser.class);
            when(mockParserBuilder.build()).thenReturn(mockJwtParser);
            Jws<Claims> jwsMock = mock(Jws.class);
            when(mockJwtParser.parseClaimsJws(token)).thenReturn(jwsMock);
            Claims mockClaims = mock(Claims.class);
            when(jwsMock.getBody()).thenReturn(mockClaims);
            TokenService underTest =  new TokenService(Duration.ofMinutes(5), keyMock);
            testLogic.accept(mockClaims, underTest);
        }
    }

    @Test
    void getUserName() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        executeWithMockClaims((claims, underTest) -> {
            String expectedUsername = "testusername";
            when(claims.getSubject()).thenReturn(expectedUsername);
            String actualUsername = assertDoesNotThrow(() -> underTest.getUserName(token));
            assertSame(expectedUsername, actualUsername);
        }, token);
    }
    @Test
    void getRoles(){
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        executeWithMockClaims((claims, underTest) -> {
            List<String> expectedRoles = List.of();
            when(claims.get("roles", List.class)).thenReturn(expectedRoles);
            List<String> actualRoles = assertDoesNotThrow(() -> underTest.getRoles(token));
            assertSame(expectedRoles, actualRoles);
        }, token);
    }
}