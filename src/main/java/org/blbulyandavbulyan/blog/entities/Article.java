package org.blbulyandavbulyan.blog.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.blbulyandavbulyan.blog.annotations.validation.article.ValidArticleText;
import org.blbulyandavbulyan.blog.annotations.validation.article.ValidArticleTitle;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "articles")
@Getter
@Setter
@NoArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long articleId;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User publisher;
    @Column(name = "title")
    @ValidArticleTitle
    private String title;
    @ValidArticleText
    @Column(name = "text", length = 5000)
    private String text;
    @OneToMany(mappedBy = "article")
    private List<Comment> comments;
    @Column(name = "publish_date")
    @CreationTimestamp
    private ZonedDateTime publishDate;
    public Article(User publisher, String title, String text) {
        this.publisher = publisher;
        this.title = title;
        this.text = text;
    }
}
