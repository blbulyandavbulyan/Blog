begin transaction;
INSERT INTO users(name, password_hash, registration_date) VALUES ('admin', '$2a$12$NOObXHh.XLsJyPAfsBzU1eWGHUE2rVvdJ8z7nD0nSWXson77nRAM.', CURRENT_TIMESTAMP);
/*вставка ролей*/
DO $$
    DECLARE admin_id BIGINT := (SELECT user_id FROM users WHERE name = 'admin');
    BEGIN
        INSERT INTO users_roles(user_id, role_id) SELECT admin_id, role_id FROM roles;
END $$;
commit;