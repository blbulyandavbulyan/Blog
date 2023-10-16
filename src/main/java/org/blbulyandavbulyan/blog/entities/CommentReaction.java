package org.blbulyandavbulyan.blog.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "comments_reactions")
@Getter
@Setter
@NoArgsConstructor
public class CommentReaction implements IReaction{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment target;
    @ManyToOne
    @JoinColumn(name = "liker_id", nullable = false)
    private User liker;
    @Column(name = "liked", nullable = false)
    private boolean liked;

    public CommentReaction(Comment target, User liker) {
        this.target = target;
        this.liker = liker;
    }
}