INSERT INTO users(name, password_hash, registration_date) VALUES('david', '$2a$12$hF488/kVTC7sCU0BRuwRLOnTHW/G1PnTn8Tu9qbJ42gA/9QZ3uZhG', CURRENT_TIMESTAMP), ('admin', '$2a$12$2YvJLlJ.L5mGR4t9JV/tx.kfMv/LwaCxIqDxBdprLnY6yZ0IDIvxG', CURRENT_TIMESTAMP), ('commenter', '$2a$12$2YvJLlJ.L5mGR4t9JV/tx.kfMv/LwaCxIqDxBdprLnY6yZ0IDIvxG', CURRENT_TIMESTAMP), ('publisher', '$2a$12$2YvJLlJ.L5mGR4t9JV/tx.kfMv/LwaCxIqDxBdprLnY6yZ0IDIvxG', CURRENT_TIMESTAMP);
INSERT INTO users_roles(user_id, role_id) VALUES (1, 1), (1, 2);
INSERT INTO users_roles(user_id, role_id) VALUES (4, 1), (3, 2);
INSERT INTO users_roles(user_id, role_id) VALUES (2, 1), (2, 2), (2, 3);
