package org.blbulyandavbulyan.blog.services;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.entities.Role;
import org.blbulyandavbulyan.blog.repositories.RoleRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    public Role getReferenceByRoleName(String roleName){
        return roleRepository.getReferenceByName(roleName);
    }
    public boolean existsByRoleName(String roleName){
        return roleRepository.existsByName(roleName);
    }
}
