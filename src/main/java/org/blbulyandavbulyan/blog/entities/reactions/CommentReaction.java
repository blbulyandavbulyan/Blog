package org.blbulyandavbulyan.blog.entities.reactions;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.blbulyandavbulyan.blog.entities.Comment;
import org.blbulyandavbulyan.blog.entities.User;

@Entity
@Table(name = "comments_reactions")
@Getter
@Setter
@NoArgsConstructor
@IdClass(ReactionId.class)
public class CommentReaction implements IReaction {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false)
    private Comment target;
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "liker_id", nullable = false)
    private User liker;
    @Column(name = "liked", nullable = false)
    private boolean liked;

    public CommentReaction(Comment target, User liker) {
        this.target = target;
        this.liker = liker;
    }
}
