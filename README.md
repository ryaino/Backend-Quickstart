
# Backend Quickstart

An all-in-one backend setup ready to go straight out of the box with Hasura, Postgres and Spring Boot.

## Features
- Auto Generated Graphql on your data
- Email and Password Role based JWT Auth.
- Fully containerized
- Only uses standard Graphql and REST, no specific client libraries required
- Easily expandable Spring Boot server for custom business logic

# How to use

## Prerequisites

- Docker with docker-compose
- Hasura CLI

     ```curl -L https://github.com/hasura/graphql-engine/raw/stable/cli/get.sh | bash```

## Setup
Before running anything you'll need to create a .env file in the root of the project and add in values for the
following properties.

- *POSTGRES_USER* : Name of the Postgres User
- *POSTGRES_PASSWORD* : Postgress Password
- *POSTGRES_DB* : Name of the Postgres database to use
- *JWT_SECRET* : Secret used to sign your JWTs. Needs to be the same between the Hasura and auth servers. Must be at least 32 characters long if using default config
- *HASURA_ADMIN_SECRET* : Password used to access the hasura console

Here's an example .env file:
```
export POSTGRES_USER=postgres
export POSTGRES_PASSWORD=password
export POSTGRES_DB=hasura_db
export DATABASE_URL=postgresql://${POSTGRES_USER}:${POSTGRES_PASSWORD}@host.docker.internal:5432/${POSTGRES_DB}
export JWT_SECRET=qwertyuioplkjhgfdsazxcvbnmqwertyuioplkjhgfdsazxcvbnm
export HASURA_ADMIN_SECRET=myadminsecretkey
```
This repo doesn't include a build of the auth part, this is to make extending it with your own functionality easier. I have, however, added a script that will build an image of this part so that it will work out of the box without having to install Maven or Java yourself. Just run the ``` buildServer.sh ``` script and the image will be created.


## Running
With the .env file filled out and the image created you're ready to run everything. Just run ``` docker-compose up```

