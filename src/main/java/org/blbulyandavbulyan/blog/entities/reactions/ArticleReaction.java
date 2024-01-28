package org.blbulyandavbulyan.blog.entities.reactions;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.blbulyandavbulyan.blog.entities.Article;
import org.blbulyandavbulyan.blog.entities.User;

@Entity
@Table(name = "articles_reactions")
@Getter
@Setter
@NoArgsConstructor
@IdClass(ReactionId.class)
public class ArticleReaction implements IReaction{
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false)
    private Article target;
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "liker_id", nullable = false)
    private User liker;

    @Column(name = "liked", nullable = false)
    private boolean liked;
    public ArticleReaction(Article target, User liker) {
        this.target = target;
        this.liker = liker;
    }
}
