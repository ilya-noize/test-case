CREATE TABLE IF NOT EXISTS wallet
(
    id      BINARY(16) NOT NULL,
    balance INTEGER NOT NULL,
    CONSTRAINT pk_wallet PRIMARY KEY (id)
);