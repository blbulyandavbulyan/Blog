package org.blbulyandavbulyan.blog.services;

import org.blbulyandavbulyan.blog.exceptions.security.AccessDeniedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SecurityServiceTest {
    private final SecurityService underTest = new SecurityService();
    @Test
    @DisplayName("executeIfExecutorIsAdminOrEqualToTarget should run if executor is admin")
    public void executeIfExecutorIsAdminOrEqualToTargetShouldRunIfExecutorIsAdmin(){
        var authentication = Mockito.mock(Authentication.class);
        var runnable = Mockito.mock(Runnable.class);
        String targetUsername = "test";
        Mockito.when(authentication.getName()).thenReturn("test2");
        Mockito.doReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))).when(authentication).getAuthorities();
        assertDoesNotThrow(()->underTest.executeIfExecutorIsAdminOrEqualToTarget(authentication, targetUsername, runnable));
        Mockito.verify(runnable, Mockito.only()).run();
    }
    @Test
    @DisplayName("executeIfExecutorIsAdminOrEqualToTarget should run if executor equal to target")
    public void executeIfExecutorIsAdminOrEqualToTargetShouldRunIfExecutorEqualToTarget(){
        var authentication = Mockito.mock(Authentication.class);
        var runnable = Mockito.mock(Runnable.class);
        String targetUsername = "test";
        Mockito.when(authentication.getName()).thenReturn(targetUsername);
        assertDoesNotThrow(()->underTest.executeIfExecutorIsAdminOrEqualToTarget(authentication, targetUsername, runnable));
        Mockito.verify(runnable, Mockito.only()).run();
    }
    @Test
    @DisplayName("executeIfExecutorIsAdminOrEqualToTarget should throw AccessDeniedException if executor is not target and not admin")
    public void executeIfExecutorIsAdminOrEqualToTargetShouldThrowIfNot(){
        var authentication = Mockito.mock(Authentication.class);
        var runnable = Mockito.mock(Runnable.class);
        String targetUsername = "test";
        Mockito.when(authentication.getName()).thenReturn("test2");
        Mockito.doReturn(List.of()).when(authentication).getAuthorities();
        assertThrows(AccessDeniedException.class, ()->underTest.executeIfExecutorIsAdminOrEqualToTarget(authentication, targetUsername, runnable));
        Mockito.verify(runnable, Mockito.never()).run();
    }
}