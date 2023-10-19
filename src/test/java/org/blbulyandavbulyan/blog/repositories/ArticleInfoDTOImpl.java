package org.blbulyandavbulyan.blog.repositories;

import org.blbulyandavbulyan.blog.dtos.article.ArticleInfoDTO;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * DTO для представления базовой информации о статье, для отправки клиенту
 */
class ArticleInfoDTOImpl implements ArticleInfoDTO {
    private final Long id;
    private final String publisherName;
    private final ZonedDateTime publishDate;
    private final String title;

    /**
     * @param id     ИД статьи
     * @param publisherName имя автора статьи
     * @param publishDate   дата публикации
     * @param title         название статьи
     */
    ArticleInfoDTOImpl(Long id, String publisherName, ZonedDateTime publishDate, String title) {
        this.id = id;
        this.publisherName = publisherName;
        this.publishDate = publishDate;
        this.title = title;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public UserDto getPublisher() {
        return () -> publisherName;
    }

    @Override
    public ZonedDateTime getPublishDate() {
        return publishDate;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ArticleInfoDTOImpl) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.publisherName, that.publisherName) &&
                Objects.equals(this.publishDate.toInstant(), that.publishDate.toInstant()) &&
                Objects.equals(this.title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, publisherName, publishDate.toInstant(), title);
    }
}
