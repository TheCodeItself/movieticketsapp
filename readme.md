# Movie Tickets App (Backend)
An API for a movie ticketing system built in a microservices architecture using Spring Boot.

The main goal of building this project is for learning purposes.

## Scope and status
This project showcases a movie ticketing system implemented using a microservices architecture. While the main goal is not to build a complete ticketing system, the goal is to implement the backend of only some of its features.

The project is currently under development.


## Technologies
The stack used in this project is listed below.
* Java 17
* Spring Boot 3 (Web, Security, Data, and Cloud)
* PostgreSQL 15

The external services used in this project are listed below.
* AWS (Cognito, Lambda, and S3)
* Stripe

## Application Architecture

The following diagram outlines the high-level architecture of the system. 

![diagram](https://github.com/larasierra/movieticketsapp/blob/master/diagram.png)

In general each service has its own data store, and they can communicate with each other by registering itself with a Service Registry.

These services are exposed through an API Gateway that also uses the Service Registry.

In addition, they interact with external services such as a user directory or a payment gateway.

## Running Using Docker Compose

You can build the application using Docker Compose running the command ```docker compose build``` in the root folder.

Once the build is complete, you can run the application with ```docker compose up --env-file=.env```. It is necessary to provide valid access keys and database connections in the .env file.

## Usage
The API is documented using Swagger. When running the application, it's possible to access to the documentation using the API Gateway's ```/swagger-ui/index.html```.

Here are some examples of the endpoints for the purchase flow: 

* GET /theater?city=
* GET /showtime?theaterId=&startDate=
* GET /seat?showtimeId=
* POST /cart/purchase-token
* POST /cart/item
* POST /order
* GET /order/{id}

Postman can be used to make requests to these endpoints.
