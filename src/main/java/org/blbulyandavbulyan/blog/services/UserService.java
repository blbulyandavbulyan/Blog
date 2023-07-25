package org.blbulyandavbulyan.blog.services;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.dtos.user.UserCreateRequest;
import org.blbulyandavbulyan.blog.dtos.user.UserInfoDTO;
import org.blbulyandavbulyan.blog.entities.User;
import org.blbulyandavbulyan.blog.exceptions.IllegalRoleNameException;
import org.blbulyandavbulyan.blog.exceptions.users.UserAlreadyExistsException;
import org.blbulyandavbulyan.blog.exceptions.users.UserNotFoundException;
import org.blbulyandavbulyan.blog.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(String userName, String password){
        if(!exists(userName)) {
            User user = new User(userName, passwordEncoder.encode(password));
            userRepository.save(user);
            return user;
        }
        else throw new UserAlreadyExistsException("User with name '" + userName + "' already exists!");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByName(username)
                .orElseThrow(()->new UsernameNotFoundException("user with given username not found!"));
    }

    public boolean exists(String username) {
        return userRepository.existsByName(username);
    }

    public User findByName(String userName) {
        return userRepository.findByName(userName).orElseThrow(()->new UserNotFoundException("user with " + userName + "was not found!"));
    }

    public void deleteById(Long id) {
        if(userRepository.existsById(id)) userRepository.deleteById(id);
        else throw new UserNotFoundException("User with id " + id + " not found!");
    }

    public User getReferenceByName(String name) {
        return userRepository.getReferenceByName(name);
    }
    @Transactional(rollbackFor = {UserAlreadyExistsException.class, IllegalRoleNameException.class})
    public void createUser(UserCreateRequest userCreateRequest) {
        User user = registerUser(userCreateRequest.name(), userCreateRequest.password());//регистрируем пользователя
        //а теперь устанавливаем ему необходимые привилегии
        for(String roleName : userCreateRequest.roleNames()){
            if(!roleService.existsByRoleName(roleName))throw new IllegalRoleNameException("Role with name + " + roleName + " not found!");
        }
        user.setRoles(userCreateRequest.roleNames().stream().map(roleService::getReferenceByRoleName).toList());
    }

    public UserInfoDTO getUserInfo(Long userId) {
        return userRepository.findByUserId(userId, UserInfoDTO.class);
    }
}
