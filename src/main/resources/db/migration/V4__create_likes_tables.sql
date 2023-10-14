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
