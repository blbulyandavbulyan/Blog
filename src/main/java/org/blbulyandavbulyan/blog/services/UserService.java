package org.blbulyandavbulyan.blog.services;

import org.blbulyandavbulyan.blog.dtos.UserRegistrationRequest;
import org.blbulyandavbulyan.blog.entities.User;
import org.blbulyandavbulyan.blog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    public User registerUser(String userName, String password){
        User user = new User(userName, passwordEncoder.encode(password));
        userRepository.save(user);
        return user;
    }

    public User registerUser(UserRegistrationRequest userRegistrationRequest) {
        return registerUser(userRegistrationRequest.username(), userRegistrationRequest.password());
    }
}
