package org.blbulyandavbulyan.blog.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
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
    @JoinColumn(name = "author_id")
    private User publisher;
    @Column(name = "text", length = 2000)
    private String text;
    @OneToMany(mappedBy = "article")
    private List<Comment> comments;
    @Column(name = "publish_date")
    @CreationTimestamp
    private Instant publishDate;

    public Article(User publisher, String text) {
        this.publisher = publisher;
        this.text = text;
        this.publishDate = Instant.now();
    }
}
