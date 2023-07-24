package org.blbulyandavbulyan.blog.repositories;

import org.blbulyandavbulyan.blog.entities.Role;
import org.springframework.data.repository.Repository;

public interface RoleRepository extends Repository<Role, Long> {
    boolean existsByName(String roleName);
    Role getReferenceByName(String roleName);
}