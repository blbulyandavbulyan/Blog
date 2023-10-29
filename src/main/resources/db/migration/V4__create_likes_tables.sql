CREATE TABLE articles_reactions
(
    id         bigserial primary key,
    liker_id   bigint  NOT NULL,
    article_id bigint  NOT NULL,
    liked      BOOLEAN NOT NULL,
    UNIQUE (liker_id, article_id),
    FOREIGN KEY (liker_id) REFERENCES users (user_id),
    FOREIGN KEY (article_id) REFERENCES articles (article_id)
);
CREATE TABLE comments_reactions
(
    id         bigserial primary key,
    liker_id   bigint  NOT NULL,
    comment_id bigint  NOT NULL,
    liked      BOOLEAN NOT NULL,
    UNIQUE (liker_id, comment_id),
    FOREIGN KEY (liker_id) REFERENCES users (user_id),
    FOREIGN KEY (comment_id) REFERENCES comments (comment_id)
);
CREATE INDEX article_id_in_reactions ON articles_reactions (article_id);
CREATE INDEX comment_id_in_reactions ON comments_reactions(comment_id);