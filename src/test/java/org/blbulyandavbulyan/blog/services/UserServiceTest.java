package org.blbulyandavbulyan.blog.services;

import org.blbulyandavbulyan.blog.dtos.user.UserCreateRequest;
import org.blbulyandavbulyan.blog.dtos.user.UserInfoDTO;
import org.blbulyandavbulyan.blog.entities.Role;
import org.blbulyandavbulyan.blog.entities.User;
import org.blbulyandavbulyan.blog.exceptions.users.UserAlreadyExistsException;
import org.blbulyandavbulyan.blog.exceptions.users.UserNotFoundException;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
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
        public PasswordEncoder passwordEncoder() {
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
    @DisplayName("loadUserByUsername throw UserNotFoundException, if the user was not found")
    @Test
    public void loadUserByUsernameShouldThrowUserNotFoundExceptionIfUserWasNotFound(){
        String userName = "david";
        Mockito.when(userRepository.findByName(userName)).thenReturn(Optional.empty());
        Mockito.when(userRepository.existsByName(userName)).thenReturn(false);
        assertThrows(UsernameNotFoundException.class, ()->userService.loadUserByUsername(userName));
    }
    @DisplayName("find by name should not throw exception, when user is exists")
    @Test
    public void findByNameWhenUserExists(){
        User expected = new User();
        expected.setName("david");
        Mockito.when(userRepository.findByName(expected.getName())).thenReturn(Optional.of(expected));
        User actual = userService.findByName(expected.getName());
        Mockito.verify(userRepository).findByName(expected.getName());
        assertEquals(expected, actual);
    }
    @DisplayName("find by name should throw UserNotFoundException, if user doesn't exist")
    @Test
    public void findByNameWhenUserDoesNotExist(){
        String userName = "david";
        Mockito.when(userRepository.findByName(userName)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, ()->userService.findByName(userName));
    }
    @DisplayName("get user info by id, when user exists")
    @Test
    public void getUserInfoByIdWhenUserExists(){
        UserInfoDTO expected = new UserInfoDTO() {
            @Override
            public Long getUserId() {
                return 1L;
            }

            @Override
            public String getName() {
                return "david";
            }

            @Override
            public List<RoleDto> getRoles() {
                return Collections.emptyList();
            }
        };
        Mockito.when(userRepository.findByUserId(expected.getUserId(), UserInfoDTO.class)).thenReturn(Optional.of(expected));
        UserInfoDTO actual = userService.getUserInfo(expected.getUserId());
        Mockito.verify(userRepository).findByUserId(expected.getUserId(), UserInfoDTO.class);
        assertEquals(expected, actual);
    }
    @DisplayName("get user info by id, when user doesn't exist")
    @Test
    public void getUserInfoWhenUserDoesNotExists(){
        Long userId = 1L;
        Mockito.when(userRepository.findByUserId(userId, UserInfoDTO.class)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, ()->userService.getUserInfo(userId));
        Mockito.verify(userRepository).findByUserId(userId, UserInfoDTO.class);
    }
    @DisplayName("create not existing user")
    @Test
    public void createNotExistingUser(){
        List<String> rolesNames = List.of("ADMIN", "USER");
        User expectedUser = new User();
        expectedUser.setUserId(1L);
        expectedUser.setName("david");
        expectedUser.setRoles(rolesNames.stream().map(Role::new).toList());
        String password = "1234";
        expectedUser.setPasswordHash(passwordEncoder.encode(password));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(expectedUser);
        Mockito.when(userRepository.existsByName(expectedUser.getName())).thenReturn(false);
        Mockito.when(roleService.existsByRoleName(Mockito.argThat(rolesNames::contains))).thenReturn(true);
        UserCreateRequest userCreateRequest = new UserCreateRequest(expectedUser.getName(), password, rolesNames);
        userService.createUser(userCreateRequest);
        Mockito.verify(userRepository).existsByName(expectedUser.getName());
        Mockito.verify(userRepository).save(
                Mockito.argThat(user -> passwordEncoder.matches(password, user.getPassword()))
        );
        rolesNames.forEach(roleName->{
            Mockito.verify(roleService).existsByRoleName(roleName);
            Mockito.verify(roleService).getReferenceByRoleName(roleName);
        });

    }
    @DisplayName("delete by id, when user exists")
    @Test
    public void deleteByIdWhenUserExists(){
        Long id = 1L;
        Mockito.when(userRepository.existsById(id)).thenReturn(true);
        userService.deleteById(id);
        Mockito.verify(userRepository).existsById(id);
        Mockito.verify(userRepository).deleteById(id);
    }
    @DisplayName("delete by id, when user doesn't exist")
    @Test
    public void deleteByIdWhenUserDoesNotExist(){
        Long id = 1L;
        Mockito.when(userRepository.existsById(id)).thenReturn(false);
        assertThrows(UserNotFoundException.class, ()->userService.deleteById(id));
        Mockito.verify(userRepository).existsById(id);
    }
    @DisplayName("get reference by name, when user exists")
    @Test
    public void getReferenceForExistingUser(){
        User expected = new User();
        expected.setName("david");
        Mockito.when(userRepository.getReferenceByName(expected.getName())).thenReturn(expected);
        Mockito.when(userRepository.existsByName(expected.getName())).thenReturn(true);
        User actual = userService.getReferenceByName(expected.getName());
        Mockito.verify(userRepository).getReferenceByName(expected.getName());
        Mockito.verify(userRepository).existsByName(expected.getName());
        assertEquals(expected, actual);
    }
    @DisplayName("get reference by name, when user doesn't exist")
    @Test
    public void getReferenceForNotExistingUser(){
        String userName = "david";
        Mockito.when(userRepository.existsByName(userName)).thenReturn(false);
        assertThrows(UserNotFoundException.class, ()->userService.getReferenceByName(userName));
        Mockito.verify(userRepository).existsByName(userName);
    }
}