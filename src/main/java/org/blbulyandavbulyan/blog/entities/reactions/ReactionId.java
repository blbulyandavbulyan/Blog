package org.blbulyandavbulyan.blog.entities.reactions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Getter
@Setter
@EqualsAndHashCode
public class ReactionId implements Serializable {
    private Long target;
    private Long liker;
}
