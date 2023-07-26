package org.blbulyandavbulyan.blog.services;

import org.blbulyandavbulyan.blog.repositories.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RoleService.class})
public class RoleServiceTest {
    @MockBean private RoleRepository roleRepository;
    @Autowired private RoleService roleService;
    @DisplayName("exist by role name, when role exists")
    @Test
    public void existByRoleNameWhenUserExists(){
        String userName = "david";
        Mockito.when(roleRepository.existsByName(userName)).thenReturn(true);
        assertTrue(roleService.existsByRoleName(userName));
        Mockito.verify(roleRepository).existsByName(userName);
    }
    @DisplayName("exist by role name, when role doesn't exist")
    @Test
    public void existByRoleNameWhenUserDoesNotExist(){
        String userName = "david";
        Mockito.when(roleRepository.existsByName(userName)).thenReturn(false);
        assertFalse(roleService.existsByRoleName(userName));
        Mockito.verify(roleRepository).existsByName(userName);
    }
}
