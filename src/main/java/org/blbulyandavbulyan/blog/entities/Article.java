package org.blbulyandavbulyan.blog.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "articles")
@Getter
@Setter
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long articleId;
    @ManyToOne
    private User publisher;
    @Column(name = "text", length = 2000)
    private String text;
    @OneToMany
    private List<Comment> comments;
    @Column(name = "publish_date")
    private ZonedDateTime publishDate;
}
