CREATE TABLE users
(
    id       bigserial primary key,
    username varchar(30) unique not null,
    password varchar(80)        not null,
    email    varchar(80)        not null unique
);

CREATE TABLE roles
(
    id   serial primary key,
    name varchar(30) unique not null
);

INSERT INTO roles(name)
VALUES ('ROLE_ADMIN');
INSERT INTO roles(name)
VALUES ('ROLE_USER');

INSERT INTO users(username, password, email)
VALUES ('user', '$2a$12$bnu0FpIUi6wPpRriIPaGw.1Me71f1AEX30ndxskUErT0FMH1P3yQm', 'user@gmail.com'),
       ('admin', '$2a$12$1ZeavEVFjT18avmZvK6/E.Sn.yN3.3Y6FfOOV9IkGmqw2tLjvq18u', 'admin@gmail.com');

CREATE TABLE users_roles
(
    user_id bigint,
    role_id int,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (role_id) REFERENCES roles (id)
);

INSERT INTO users_roles(user_id, role_id) VALUES (1,2), (2,1)