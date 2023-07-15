CREATE TABLE roles(role_id bigserial PRIMARY KEY, name VARCHAR(255) NOT NULL);
CREATE TABLE users(user_id bigserial PRIMARY KEY, name VARCHAR(255) NOT NULL UNIQUE, password_hash VARCHAR(255) NOT NULL, registration_date TIMESTAMP WITH TIME ZONE NOT NULL);
CREATE TABLE users_roles(id bigserial PRIMARY KEY, user_id INTEGER NOT NULL, role_id INTEGER NOT NULL, FOREIGN KEY(user_id) REFERENCES users(user_id), FOREIGN KEY role_id REFERENCES roles(role_id));
CREATE TABLE articles(article_id bigserial PRIMARY KEY, author_id INTEGER NOT NULL, title VARCHAR(255) NOT NULL, text VARCHAR(5000) NOT NULL, publish_date TIMESTAMP WITH TIME ZONE NOT NULL, FOREIGN KEY(author_id) REFERENCES users(user_id));
CREATE TABLE comments(comment_id bigserial PRIMARY KEY, author_id INTEGER NOT NULL, article_id INTEGER NOT NULL, text VARCHAR(2000) NOT NULL, publish_date TIMESTAMP WITH TIME ZONE NOT NULL, FOREIGN KEY(author_id) REFERENCES users(user_id), FOREIGN KEY(article_id) REFERENCES articles(article_id));
