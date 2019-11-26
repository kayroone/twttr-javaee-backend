# twttr RESTful API

## Configure the application

Since we're going to start the application with a full environment including a postgres database and a keycloak auth 
service we need to define the following hostnames inside the hostsystems hosts file:

    twttr.database
    keycloak.service
    
This is necessary because the keycloak auth service will redirect the user to the keycloak containers hostname within 
OAuth2 Lifecycle after a successful login. Also the database has a default configured hostname inside the API service 
container.   

## Starting the application

To start the twttr API with a full environment just execute the docker-compose.yaml file with:

    docker-compose up src/main/resources/docker/docker-compose.yml 

This will start the following containers:

1. PostgreSQL database container with init.sql script creating the twttr and keycloak databases.
2. Keycloak auth service container. If you don't want to configure keycloak yourself you could use the following 
default configuration file and import it to keycloak:

        src/test/resources/keycloak-realm-export.json
    
3. twttr RESTful API container using the postgreSQL database and keycloak auth service container.

The application is now reachable via:

    http://localhost:8081/service/hello
    http://localhost:8081/service/tweets
    http://localhost:8081/service/users
    
For a detailed API documentation see the next article about the Swagger API documentation.
 
## Swagger API documentation

Once the application is started you can visit the following URL for a detailed API documentation:

    http://localhost:8081/swagger-ui
