# Authorify<br>

This is a Spring Boot microservice for authentication and authorization using PostgreSQL.

After registration, the service sends an email to the registering email with a confirmation link. This link must be used within 15 mins.

## Requirements

* Java 11
* Maven
* PostgreSQL

## Getting Started

1. Clone this repository.
2. Create a file called `application.properties` in the root of the project.
3. In the `application.properties` file, add the following properties:

    *spring.datasource.url=jdbc:postgresql://localhost:5432/authorify*
    
    *spring.datasource.username=postgres*
    
    *spring.datasource.password=postgres*

4. Run the following command to build and start the application:

    *mvn spring-boot:run*
    
---

API

The API provides the following endpoints:

> /api/auth/login: Login a user.
>
> /api/auth/register: Register a new user.
>
> /api/auth/refresh: Refresh an access token.
>
> /api/auth/me: Get the current user.
>
---
Troubleshooting

If you are having trouble getting the application to start, please check the following:

- Make sure that you have Java 11 installed.

- Make sure that you have Maven installed.

- Make sure that you have PostgreSQL installed and running.

- Make sure that you have added your PostgreSQL database credentials to the application.properties file.

Anyone can use it in their project, just configure the database server to yours.

---


