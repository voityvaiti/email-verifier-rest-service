CREATE TABLE password_reset_token
(
    id              UUID DEFAULT gen_random_uuid(),
    user_id         UUID UNIQUE,
    token           VARCHAR(255) NOT NULL,
    expiry_datetime TIMESTAMP    NOT NULL,
    created_date TIMESTAMPTZ,
    updated_date TIMESTAMPTZ,
    FOREIGN KEY (user_id) REFERENCES usr (id) ON UPDATE CASCADE ON DELETE CASCADE,
    PRIMARY KEY (id)
);