package org.blbulyandavbulyan.blog.repositories;

import org.blbulyandavbulyan.blog.entities.User;

public class RepositoryTestUtils {

    public static User createAndSaveUser(UserRepository userRepository) {
        User publisher = new User();
        publisher.setName("davdfdsfafid");
        publisher.setPasswordHash("fdfdf");
        return userRepository.saveAndFlush(publisher);
    }

    public static User createAndSaveUser(UserRepository userRepository, String username){
        return userRepository.save(new User(username, "eafafaw"));
    }
}
