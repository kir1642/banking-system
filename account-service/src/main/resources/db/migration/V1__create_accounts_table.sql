CREATE TABLE accounts (
                          id BIGSERIAL PRIMARY KEY,
                          owner_name VARCHAR(255) NOT NULL,
                          balance NUMERIC(19,2) NOT NULL,
                          currency VARCHAR(10) NOT NULL
);
