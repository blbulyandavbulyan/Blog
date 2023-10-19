package org.blbulyandavbulyan.blog.dtos.article;

import java.time.ZonedDateTime;

public interface ArticleInfoDTO {
    Long getId();

    UserDto getPublisher();

    ZonedDateTime getPublishDate();

    String getTitle();
  
    interface UserDto{
        String getName();
    }
}
