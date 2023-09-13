package org.blbulyandavbulyan.blog.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.blbulyandavbulyan.blog.annotations.validation.comment.ValidCommentText;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;
    @ValidCommentText
    @Column(name = "text", length = 2000)
    private String text;
    @Column(name = "publish_date")
    @CreationTimestamp
    private ZonedDateTime publishDate;
}
