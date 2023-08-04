package org.blbulyandavbulyan.blog.dtos.roles;


import java.util.List;

public record UpdateRolesDto(Long userId, List<String> rolesNames) {
}
