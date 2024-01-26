CREATE TABLE roles
(
    role_id bigserial PRIMARY KEY,
    name    VARCHAR(255) NOT NULL UNIQUE
);
CREATE TABLE users
(
    user_id           bigserial PRIMARY KEY,
    name              VARCHAR(255)             NOT NULL UNIQUE,
    password_hash     VARCHAR(255)             NOT NULL,
    tfa_secret        VARCHAR(120)             DEFAULT NULL,
    tfa_enabled       BOOLEAN NOT NULL DEFAULT FALSE,
    registration_date TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE TABLE users_roles
(
    id      bigserial PRIMARY KEY,
    user_id INTEGER NOT NULL,
    role_id INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (role_id),
    UNIQUE (user_id, role_id)
);
CREATE TABLE articles
(
    article_id   bigserial PRIMARY KEY,
    author_id    INTEGER                  NOT NULL,
    title        VARCHAR(255)             NOT NULL,
    text         VARCHAR(5000)            NOT NULL,
    publish_date TIMESTAMP WITH TIME ZONE NOT NULL,
    FOREIGN KEY (author_id) REFERENCES users (user_id) ON DELETE CASCADE
);
CREATE TABLE comments
(
    comment_id   bigserial PRIMARY KEY,
    author_id    INTEGER                  NOT NULL,
    article_id   INTEGER                  NOT NULL,
    text         VARCHAR(2000)            NOT NULL,
    publish_date TIMESTAMP WITH TIME ZONE NOT NULL,
    FOREIGN KEY (author_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (article_id) REFERENCES articles (article_id) ON DELETE CASCADE
);
