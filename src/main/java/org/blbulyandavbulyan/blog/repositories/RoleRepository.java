package org.blbulyandavbulyan.blog.repositories;

import org.blbulyandavbulyan.blog.entities.Role;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface RoleRepository extends Repository<Role, Long> {
    boolean existsByRoleName(String roleName);
    Role getReferenceByRoleName(String roleName);
}