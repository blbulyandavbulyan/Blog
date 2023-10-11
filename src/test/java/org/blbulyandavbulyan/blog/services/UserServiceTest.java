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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Spy
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleService roleService;

    @InjectMocks
    private UserService underTest;
    @DisplayName("testing registration not existing user")
    @Test public void testRegisterNotExistingUser(){
        String userName = "david";
        String password = "1234";
        User expectedUser = new User();
        expectedUser.setName(userName);
        expectedUser.setPasswordHash(passwordEncoder.encode(password));
        expectedUser.setUserId(1L);
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);
        when(userRepository.existsByName(userName)).thenReturn(false);
        User user = underTest.registerUser(userName, password);
        verify(userRepository).existsByName(userName);
        assertEquals(userName, user.getName());
        assertTrue(passwordEncoder.matches(password, user.getPassword()));
        assertEquals(expectedUser.getUserId(), user.getUserId());
    }
    @DisplayName("testing registration existing user")
    @Test
    public void testRegisterExistingUser(){
        String userName = "david";
        when(userRepository.existsByName(userName)).thenReturn(true);
        assertThrows(UserAlreadyExistsException.class, ()-> underTest.registerUser(userName, "1234"));
    }
    @DisplayName("exists test, when user really exists")
    @Test
    public void testExistUser(){
        String userName = "david";
        when(userRepository.existsByName(userName)).thenReturn(true);
        boolean exists = underTest.exists(userName);
        verify(userRepository).existsByName(userName);
        assertTrue(exists);
    }
    @DisplayName("exists test, when user doesn't exist")
    @Test
    public void testDoesNotExistUser(){
        String userName = "david";
        when(userRepository.existsByName(userName)).thenReturn(false);
        boolean exists = underTest.exists(userName);
        verify(userRepository).existsByName(userName);
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
        when(userRepository.findByName(userName)).thenReturn(Optional.of(user));
        UserDetails userDetails = underTest.loadUserByUsername(userName);
        verify(userRepository).findByName(userName);
        assertEquals(user.getName(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertEquals(user.getAuthorities(), userDetails.getAuthorities());
    }
    @DisplayName("loadUserByUsername throw UserNotFoundException, if the user was not found")
    @Test
    public void loadUserByUsernameShouldThrowUserNotFoundExceptionIfUserWasNotFound(){
        String userName = "david";
        when(userRepository.findByName(userName)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, ()-> underTest.loadUserByUsername(userName));
    }
    @DisplayName("find by name should not throw exception, when user is exists")
    @Test
    public void findByNameWhenUserExists(){
        User expected = new User();
        expected.setName("david");
        when(userRepository.findByName(expected.getName())).thenReturn(Optional.of(expected));
        User actual = underTest.findByName(expected.getName());
        verify(userRepository).findByName(expected.getName());
        assertEquals(expected, actual);
    }
    @DisplayName("find by name should throw UserNotFoundException, if user doesn't exist")
    @Test
    public void findByNameWhenUserDoesNotExist(){
        String userName = "david";
        when(userRepository.findByName(userName)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, ()-> underTest.findByName(userName));
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
        when(userRepository.findByUserId(expected.getUserId(), UserInfoDTO.class)).thenReturn(Optional.of(expected));
        UserInfoDTO actual = underTest.getUserInfo(expected.getUserId());
        verify(userRepository).findByUserId(expected.getUserId(), UserInfoDTO.class);
        assertEquals(expected, actual);
    }
    @DisplayName("get user info by id, when user doesn't exist")
    @Test
    public void getUserInfoWhenUserDoesNotExists(){
        Long userId = 1L;
        when(userRepository.findByUserId(userId, UserInfoDTO.class)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, ()-> underTest.getUserInfo(userId));
        verify(userRepository).findByUserId(userId, UserInfoDTO.class);
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
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);
        when(userRepository.existsByName(expectedUser.getName())).thenReturn(false);
        when(roleService.existsByRoleName(argThat(rolesNames::contains))).thenReturn(true);
        UserCreateRequest userCreateRequest = new UserCreateRequest(expectedUser.getName(), password, rolesNames);
        underTest.createUser(userCreateRequest);
        verify(userRepository).existsByName(expectedUser.getName());
        verify(userRepository).save(
                argThat(user -> passwordEncoder.matches(password, user.getPassword()))
        );
        rolesNames.forEach(roleName->{
            verify(roleService).existsByRoleName(roleName);
            verify(roleService).getReferenceByRoleName(roleName);
        });

    }
    @DisplayName("delete by id, when user exists")
    @Test
    public void deleteByIdWhenUserExists(){
        Long id = 1L;
        when(userRepository.existsById(id)).thenReturn(true);
        underTest.deleteById(id);
        verify(userRepository).existsById(id);
        verify(userRepository).deleteById(id);
    }
    @DisplayName("delete by id, when user doesn't exist")
    @Test
    public void deleteByIdWhenUserDoesNotExist(){
        Long id = 1L;
        when(userRepository.existsById(id)).thenReturn(false);
        assertThrows(UserNotFoundException.class, ()-> underTest.deleteById(id));
        verify(userRepository).existsById(id);
    }
    @DisplayName("get reference by name, when user exists")
    @Test
    public void getReferenceForExistingUser(){
        User expected = new User();
        expected.setName("david");
        when(userRepository.getReferenceByName(expected.getName())).thenReturn(expected);
        when(userRepository.existsByName(expected.getName())).thenReturn(true);
        User actual = underTest.getReferenceByName(expected.getName());
        verify(userRepository).getReferenceByName(expected.getName());
        verify(userRepository).existsByName(expected.getName());
        assertEquals(expected, actual);
    }
    @DisplayName("get reference by name, when user doesn't exist")
    @Test
    public void getReferenceForNotExistingUser(){
        String userName = "david";
        when(userRepository.existsByName(userName)).thenReturn(false);
        assertThrows(UserNotFoundException.class, ()-> underTest.getReferenceByName(userName));
        verify(userRepository).existsByName(userName);
    }

    @Test
    @DisplayName("updateUserPassword when user exists")
    void updateUserPasswordWhenUserExists() {
        String targetUsername = "david";
        String password = "testpassword";
        when(userRepository.updatePasswordHashByName(anyString(), eq(targetUsername))).thenReturn(1);
        assertDoesNotThrow(()-> underTest.updateUserPassword(targetUsername, password));
        ArgumentCaptor<String> passwordHashArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(userRepository, only()).updatePasswordHashByName(passwordHashArgumentCaptor.capture(), eq(targetUsername));
        assertTrue(passwordEncoder.matches(password, passwordHashArgumentCaptor.getValue()));
    }

    @Test
    @DisplayName("updateUserPassword when user doesn't exist")
    void updateUserPasswordWhenUserDoesNotExist() {
        String targetUsername = "david";
        String password = "testpassword";
        when(userRepository.updatePasswordHashByName(anyString(), eq(targetUsername))).thenReturn(0);
        assertThrows(UserNotFoundException.class, ()-> underTest.updateUserPassword(targetUsername, password));
    }

    @Test
    @DisplayName("updateRoles when user exists")
    void updateRolesWhenUserExists() {
        long userId = 1L;
        User user = mock(User.class);
        List<String> rolesNames = List.of("ROLE_COMMENTER", "ROLE_PUBLISHER");
        HashMap<String, Role> nameToRole = new HashMap<>();
        rolesNames.forEach(e->nameToRole.put(e, new Role(e)));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleService.getReferenceByRoleName(anyString()))
                .then(invocation -> nameToRole.get(invocation.getArgument(0, String.class)));
        assertDoesNotThrow(()-> underTest.updateRoles(userId, rolesNames));
        verify(userRepository, times(1)).save(user);
        rolesNames.forEach(roleName->verify(roleService, times(1)).getReferenceByRoleName(roleName));
        verifyNoMoreInteractions(roleService);
        ArgumentCaptor<List<Role>> rolesCaptor = ArgumentCaptor.forClass(List.class);
        verify(user, only()).setRoles(rolesCaptor.capture());
        List<Role> actualRoles = rolesCaptor.getValue();
        assertTrue(actualRoles.containsAll(nameToRole.values()));
        assertEquals(nameToRole.values().size(), actualRoles.size());
    }

    @Test
    @DisplayName("updateRoles when user doesn't exist")
    void updateRolesWhenUserDoesNotExists() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, ()-> underTest.updateRoles(userId, List.of()));
    }
}