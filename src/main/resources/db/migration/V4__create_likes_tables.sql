CREATE TABLE articles_reactions
(
    liker_id   bigint  NOT NULL,
    target_id  bigint  NOT NULL,
    liked      BOOLEAN NOT NULL,
    PRIMARY KEY (liker_id, target_id),
    FOREIGN KEY (liker_id) REFERENCES users (user_id),
    FOREIGN KEY (target_id) REFERENCES articles (article_id)
);
CREATE TABLE comments_reactions
(
    liker_id   bigint  NOT NULL,
    target_id  bigint  NOT NULL,
    liked      BOOLEAN NOT NULL,
    PRIMARY KEY (liker_id, target_id),
    FOREIGN KEY (liker_id) REFERENCES users (user_id),
    FOREIGN KEY (target_id) REFERENCES comments (comment_id)
);
CREATE INDEX article_id_in_reactions ON articles_reactions (target_id);
CREATE INDEX comment_id_in_reactions ON comments_reactions(target_id);