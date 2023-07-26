package org.blbulyandavbulyan.blog.repositories;

import org.blbulyandavbulyan.blog.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String username);
    boolean existsByName(String username);

    User getReferenceByName(String name);

    <T> Optional<T> findByUserId(Long userId, Class<T> dtoClass);
}
