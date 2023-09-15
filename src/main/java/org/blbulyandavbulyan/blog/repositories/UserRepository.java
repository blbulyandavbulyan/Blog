package org.blbulyandavbulyan.blog.repositories;

import org.blbulyandavbulyan.blog.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    @Transactional
    @Modifying
    @Query("update User u set u.passwordHash = ?1 where u.name = ?2")
    int updatePasswordHashByName(String passwordHash, String name);
    Optional<User> findByName(String username);
    boolean existsByName(String username);

    User getReferenceByName(String name);

    <T> Optional<T> findByUserId(Long userId, Class<T> dtoClass);
}
