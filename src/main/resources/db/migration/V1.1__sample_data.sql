INSERT INTO usr (email, password, enabled, created_date, updated_date)
VALUES ('admin@mail.com', '$2a$12$BeVw4QfchnRgPb03TIatfe3oJxnVgn7VStu0Z96WfVW9608u.AMTm', TRUE, current_timestamp,
        current_timestamp),
       ('user@mail.com', '$2a$12$GtzlB3sErx8KUuyaxjfwd.pHuSv45006CLm7d.HAcsfTSUDgos3hi', TRUE, current_timestamp,
        current_timestamp);


INSERT INTO users_roles (user_id, role)
VALUES ((SELECT id FROM usr WHERE email = 'admin@mail.com' LIMIT 1), 'ADMIN'),
       ((SELECT id FROM usr WHERE email = 'user@mail.com' LIMIT 1), 'USER');