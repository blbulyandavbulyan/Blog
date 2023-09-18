package org.blbulyandavbulyan.blog.services;

import org.blbulyandavbulyan.blog.entities.Role;
import org.blbulyandavbulyan.blog.exceptions.role.IllegalRoleNameException;
import org.blbulyandavbulyan.blog.repositories.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RoleService.class})
public class RoleServiceTest {
    @MockBean private RoleRepository roleRepository;
    @Autowired private RoleService roleService;
    @DisplayName("exist by role name, when role exists")
    @Test
    public void existByRoleNameWhenUserExists(){
        String roleName = "ADMIN";
        Mockito.when(roleRepository.existsByName(roleName)).thenReturn(true);
        assertTrue(roleService.existsByRoleName(roleName));
        Mockito.verify(roleRepository).existsByName(roleName);
    }
    @DisplayName("exist by role name, when role doesn't exist")
    @Test
    public void existByRoleNameWhenUserDoesNotExist(){
        String roleName = "ADMIN";
        Mockito.when(roleRepository.existsByName(roleName)).thenReturn(false);
        assertFalse(roleService.existsByRoleName(roleName));
        Mockito.verify(roleRepository).existsByName(roleName);
    }
    @DisplayName("get reference by name when role exists")
    @Test
    public void getReferenceByNameWhenRoleExists(){
        String roleName = "ADMIN";
        Role expectedRole = new Role(roleName);
        Mockito.when(roleRepository.getReferenceByName(roleName)).thenReturn(Optional.of(expectedRole));
        assertEquals(expectedRole, roleService.getReferenceByRoleName(roleName));
        Mockito.verify(roleRepository).getReferenceByName(roleName);
    }
    @DisplayName("get reference by name when role doesn't exist")
    @Test
    public void getReferenceByNameWhenRoleDoesNotExist(){
        String roleName = "NOTFOUND";
        Mockito.when(roleRepository.getReferenceByName(roleName)).thenReturn(Optional.empty());
        assertThrows(IllegalRoleNameException.class, ()->roleService.getReferenceByRoleName(roleName));
    }
}
