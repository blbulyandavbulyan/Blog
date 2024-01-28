package org.blbulyandavbulyan.blog.entities.reactions;

import lombok.*;

import java.io.Serializable;
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ReactionId implements Serializable {
    private Long target;
    private Long liker;
}
