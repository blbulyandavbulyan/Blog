package org.blbulyandavbulyan.blog.services;

import org.blbulyandavbulyan.blog.entities.User;
import org.blbulyandavbulyan.blog.exceptions.users.UserAlreadyExistsException;
import org.blbulyandavbulyan.blog.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserService.class, UserServiceTest.TestConfig.class})
class UserServiceTest {
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private RoleService roleService;

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @TestConfiguration
    static class TestConfig {

        @Bean
        public PasswordEncoder meterRegistry() {
            return new BCryptPasswordEncoder();
        }

    }
    @DisplayName("testing registration not existing user")
    @Test public void testRegisterNotExistingUser(){
        String userName = "david";
        String password = "1234";
        User expectedUser = new User();
        expectedUser.setName(userName);
        expectedUser.setPasswordHash(passwordEncoder.encode(password));
        expectedUser.setUserId(1L);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(expectedUser);
        Mockito.when(userRepository.existsByName(userName)).thenReturn(false);
        User user = userService.registerUser(userName, password);
        Mockito.verify(userRepository).existsByName(userName);
        assertEquals(userName, user.getName());
        assertTrue(passwordEncoder.matches(password, user.getPassword()));
        assertEquals(expectedUser.getUserId(), user.getUserId());
    }
    @DisplayName("testing registration existing user")
    @Test
    public void testRegisterExistingUser(){
        String userName = "david";
        Mockito.when(userRepository.existsByName(userName)).thenReturn(true);
        assertThrows(UserAlreadyExistsException.class, ()->userService.registerUser(userName, "1234"));
    }
    @DisplayName("exists test, when user really exists")
    @Test
    public void testExistUser(){
        String userName = "david";
        Mockito.when(userRepository.existsByName(userName)).thenReturn(true);
        boolean exists = userService.exists(userName);
        Mockito.verify(userRepository).existsByName(userName);
        assertTrue(exists);
    }
    @DisplayName("exists test, when user doesn't exist")
    @Test
    public void testDoesNotExistUser(){
        String userName = "david";
        Mockito.when(userRepository.existsByName(userName)).thenReturn(false);
        boolean exists = userService.exists(userName);
        Mockito.verify(userRepository).existsByName(userName);
        assertFalse(exists);
    }
    @DisplayName("load user by user name, when user exists")
    @Test
    public void loadUserByUserNameWhenUserExists(){
        String userName = "david";
        User user = new User();
        user.setName(userName);
        user.setPasswordHash("dadada");
        user.setRoles(Collections.emptyList());
        user.setArticles(Collections.emptyList());
        user.setRegistrationDate(ZonedDateTime.now());
        Mockito.when(userRepository.findByName(userName)).thenReturn(Optional.of(user));
        UserDetails userDetails = userService.loadUserByUsername(userName);
        Mockito.verify(userRepository).findByName(userName);
        assertEquals(user.getName(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertEquals(user.getAuthorities(), userDetails.getAuthorities());
    }
}