CREATE TABLE users(user_id bigserial PRIMARY KEY, name VARCHAR(255) NOT NULL UNIQUE, registration_date TIMESTAMP WITH TIME ZONE NOT NULL);
CREATE TABLE articles(article_id bigserial PRIMARY KEY, author_id INTEGER, text VARCHAR(5000) NOT NULL, publish_date TIMESTAMP WITH TIME ZONE NOT NULL);
CREATE TABLE comments(comment_id bigserial PRIMARY KEY, author_id INTEGER, article_id INTEGER, text VARCHAR(2000) NOT NULL, publish_date TIMESTAMP WITH TIME ZONE NOT NULL);
