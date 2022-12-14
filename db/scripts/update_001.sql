CREATE TABLE session (
    id SERIAL PRIMARY KEY,
    name text
);

CREATE TABLE client (
    id SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    email VARCHAR NOT NULL UNIQUE,
    phone VARCHAR NOT NULL UNIQUE
);

CREATE TABLE ticket
(
    id         SERIAL PRIMARY KEY,
    session_id INT NOT NULL REFERENCES session (id),
    row        INT NOT NULL,
    cell       INT NOT NULL,
    client_id    INT NOT NULL REFERENCES client (id)
);

