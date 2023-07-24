package org.blbulyandavbulyan.blog.services;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.entities.User;
import org.blbulyandavbulyan.blog.exceptions.users.UserAlreadyExistsException;
import org.blbulyandavbulyan.blog.exceptions.users.UserNotFoundException;
import org.blbulyandavbulyan.blog.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
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
}
