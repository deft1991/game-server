CREATE DATABASE authorization_server;

create user authorization_server with encrypted password 'authorization_server';
grant all privileges on database authorization_server to authorization_server;

--You are now connected to database "authorization_server" as user "postgres".
GRANT ALL ON SCHEMA public TO authorization_server;
