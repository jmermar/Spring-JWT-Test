# About
I've been learning JWT authentication with Spring Security and thus created this simple RestFul API.

# How it works
It consists of 3 Endpoints, "/users/register" will generate an user with an email and a password. "/users/login" will log the user and generate the session token. Finally, "/users/aboutme" is a secured endpoint that will display the user entry in database and will read the user email from a valid user token.
