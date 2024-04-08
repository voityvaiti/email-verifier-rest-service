CREATE TABLE usr
(
    id           UUID DEFAULT gen_random_uuid(),
    email        VARCHAR(255) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    enabled      BOOLEAN      NOT NULL,
    created_date TIMESTAMPTZ,
    updated_date TIMESTAMPTZ,
    PRIMARY KEY (id)
);

CREATE TABLE users_roles
(
    id      UUID DEFAULT gen_random_uuid(),
    user_id UUID,
    role    VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES usr (id) ON UPDATE CASCADE ON DELETE CASCADE,
    PRIMARY KEY (id)
)