CREATE DATABASE authorization_server;

create userRedis authorization_server with encrypted password 'authorization_server';
grant all privileges on database authorization_server to authorization_server;