
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
This repo doesn't include a build of the auth part, but instead the raw source code, this is to make extending it with your own functionality and custom business logic easier. I have, however, added a script that will build an image of this part so that it will work out of the box without having to install Maven or Java yourself. Just run the ``` buildServer.sh ``` script and the image will be created.


## Running
With the .env file filled out and the image created you're ready to run everything. Just run ``` docker-compose up``` and all 3 services will start up.

### Hasura
 head over to http://localhost:8180/console and you'll be able to see the Hasura console. If you need to input a password then this is the same as the HASURA_ADMIN_SECRET value you put in the .env file. Having a look around you probably won't see anything here. To get things set up here go back to your terminal and run the ``` dbSetup.sh ``` script. What this does is apply a predefined set of sql statements to your database alongside hasura specific settings. Refresh the console after running the script and there should be a couple of new things there. Here's how I've set up the database to start with.

 ![db](database-layout.png)

I chose to do it this way because it leaves things a bit easier to alter and change how you want. Rather than tracking user roles on the users table you get a lot more flexibility this way I think. The user_roles table is set as an Hasurs Enum table but NOT a Postgres Enum table. Adding and removing values from here shouldn't be done frequently as you have to manually reload the hasura metadata every time you do this.

The user_roles table is already pre-filled with ROLE_USER which is automatically applied to every new user registration. Hasura is also set up with the "anonymous" role which will be applied to any unauthorized requests sent to the server.

If you want to make some changes to the structure of the database or change your hasura settings then DO NOT do so here. In order to have hasura auto generate new migration and metadata files you will need to run the console using the hasura cli. Run the ```startConsole.sh``` script to start up a copy of the hasura console that listens for changes in real time. Check the terminal output for what port to visit to view this. Once that's running you can add or change all the database tables to your heart's desire. While doing all this you can check your source control for the project at any time and you should see the auto generated migrations and metadata to keep track of these changes.

From this point you're ready to start interacting with the server. You'll be able to find the graphql endpoint on the API tab of the console. If you haven't changed the configuration at all then for now it shoud just be available using a POST at ```http://localhost:8180/v1/graphql```.


## Authentication and Authorization

### Register New User
Send a POST request to ```localhost:8080/api/user/register``` with the email and password in the body. The email must be one that does not already exist for another user.
```
{
    "email": "john@doe.com",
    "password": "password"
}
```
This will create a new user in the database alongside a hash of their password. An entry will also be added to the ```assigned_user_roles``` table granting the new user the ```ROLE_USER``` role.

### Login Existing User
Send a POST request to ```localhost:8080/api/login```
The email and password will need to be sent in form syntax rather than a json body. As a response you will receive a JWT with an expiration of 10 minutes, alongside a refresh token with an expiration of 30 minutes.

### Refreshing with the refresh token
Send a GET request to ```localhost:8080/api/user/refresh``` using the refresh token in the ```Authorization``` header, prefixed with ```Bearer```. The response will send back a new JWT, if it doesn't then the refresh token has expired and you must log in again.

### How the JWT works with Hausra
One of the amazing things about Hasura is the role based rules that it allows you to setup when accessing your data. The JWT you receive from logging in contains a couple of values that have been specifically added for use with Hasura. The most important of these will be the ```x-hasura-allowed-roles``` which contains a list of all roles applied to a user. These are generated using the assigned_user_roles table when logging in and are what allow you to configure your security rules in hasura.