CREATE TABLE main.users (
                            id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            email           VARCHAR(255) NOT NULL UNIQUE,
                            password_hash   VARCHAR(255) NOT NULL,
                            name            VARCHAR(255) NOT NULL,
                            role            VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
                            address         TEXT,
                            created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_email ON main.users(email);