package org.blbulyandavbulyan.blog.dtos.user;

import java.util.List;

public interface UserInfoDTO {

    Long getUserId();

    String getName();

    List<RoleDto> getRoles();
    interface RoleDto{
        String getName();
    }

}
