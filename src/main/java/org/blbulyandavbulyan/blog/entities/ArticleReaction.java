package org.blbulyandavbulyan.blog.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "articles_reactions")
@Getter
@Setter
@NoArgsConstructor
public class ArticleReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;
    @ManyToOne
    @JoinColumn(name = "liker_id", nullable = false)
    private User liker;
    @Column(name = "liked", nullable = false)
    private boolean liked;
    public ArticleReaction(Article article, User liker) {
        this.article = article;
        this.liker = liker;
    }
}
