package org.blbulyandavbulyan.blog.dtos.user;

import java.util.List;

public record UserInfoDTO(Long userId, String name, List<String> roleNames) {
}
