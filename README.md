
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

     ```
     curl -L https://github.com/hasura/graphql-engine/raw/stable/cli/get.sh | bash
     ```

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
With the .env file filled out and the image created you're ready to run everything. Just run ``` docker-compose up``` and all 3 services will start up.

### Hasura
 head over to http://localhost:8180/console and you'll be able to see the Hasura console. If you need to input a password then this is the same as the HASURA_ADMIN_SECRET value you put in the .env file. Having a look around you probably won't see anything here. To get things set up here go back to your terminal and run the ``` dbSetup.sh ``` script. What this does is apply a predefined set of sql statements to your database alongside hasura specific settings. Refresh the console after running the script and there should be a couple of new things there. Here's how I've set up the database to start with.

 ![db](database-layout.png)

I chose to do it this way because it leaves things a bit easier to alter and change how you want. Rather than tracking user roles on the users table you get a lot more flexibility this way I think. The user_roles table is set as an Hasurs Enum table but NOT a Postgres Enum table. Adding and removing values from here shouldn't be done frequently as you have to manually reload the hasura metadata every time you do this.

If you want to make some changes to the structure of the database or change your hasura settings the DO NOT do so here. In order to have hasura auto generate new migration and metadata files you will need to run the console using the hasura cli. Run the ```startConsole.sh``` script to start up a copy of the hasura console that listens for changes in real time. Check the terminal output for what port to visit to view this. Once that's running you can add or change all the database tables to your heart's desire. While doing all this you can check your source control for the project at any time and you should see the auto generated migrations and metadata to keep track of these changes.
