CREATE DATABASE keycloak;
CREATE USER keycloak WITH PASSWORD 'keycloak';
GRANT ALL PRIVILEGES ON DATABASE keycloak to keycloak;

CREATE DATABASE twttr;
CREATE USER twttr WITH PASSWORD 'twttr';
GRANT ALL PRIVILEGES ON DATABASE twttr to twttr;
