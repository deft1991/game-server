CREATE TABLE IF NOT EXISTS oauth_client_details
(
    client_id               CHARACTER varying(255) NOT NULL,
    client_secret           CHARACTER VARYING(255) NOT NULL,
    web_server_redirect_uri CHARACTER VARYING(2048) DEFAULT NULL,
    scope                   CHARACTER VARYING(255)  DEFAULT NULL,
    access_token_validity   INTEGER                 DEFAULT NULL,
    refresh_token_validity  INTEGER                 DEFAULT NULL,
    resource_ids            CHARACTER VARYING(1024) DEFAULT NULL,
    authorized_grant_types  CHARACTER VARYING(1024) DEFAULT NULL,
    authorities             CHARACTER VARYING(1024) DEFAULT NULL,
    additional_information  CHARACTER VARYING(4096) DEFAULT NULL,
    autoapprove             CHARACTER VARYING(255)  DEFAULT NULL,
    PRIMARY KEY (client_id)
);
INSERT INTO oauth_client_details
(client_id,
 client_secret,
 web_server_redirect_uri,
 scope,
 access_token_validity,
 refresh_token_validity,
 resource_ids,
 authorized_grant_types,
 additional_information)
VALUES ('web',
        '$2a$10$tXpi8/ztOexl5.VJiIViluMXZh//G0l8KZER/ixdU02St7bPMClt2',
        NULL,
        'READ,WRITE',
        '3600',
        '10000',
        'product-service',
        'authorization_code,password,refresh_token,implicit',
        '{}');
