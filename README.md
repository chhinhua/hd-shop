# Web Development Project - Fashion E-commerce Site (BACKEND)

This is a RESTful API for managing Fashion E-commerce App build using the Spring Boot framework.

## Table of Contents
- [Description](#description)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [API Documentation](#api-documentation)
- [Running the Tests](#running-the-tests)
- [Features](#features)
- [Built With](#built-with)
- [Authors](#authors)
- [Project management](#project-management)

## Description

This application is a web application that allows users to add cart, place order, track order, favorite and rate products, search and filter products. There are also features such as order management, personal information management,... and other statistical management features of the admin. . The API endpoints are designed to be RESTful, making it easy to interact with the application programmatically.

## Getting Started

To get started with this project, clone the repository and run it in your local environment. The following instructions will guide you through the process:

### Prerequisites

- Java 17 or higher
- Maven 3.6.0 or higher

### Installation

1. Clone the repository:

   ```
   git clone https://github.com/chhinhua/duck-shop.git
2. Navigate to the project directory:

   ```
   cd duck-shop
3. Build and run the project:

   ```
   mvn spring-boot:run
   
## API Documentation

The API documentation is available at /swagger-ui/index.html. You can access it by opening a web browser and navigating to http://localhost:8080/swagger-ui/index.html.

## Running the Tests

To run the tests, navigate to the project directory and execute the following command:
   
    mvn test
   
   
## Features
- User:
    - Auth: Signup, signin, change password, recover password
    - Search, filter product
    - Favorite & rating product, ...
    - Cart management: add cart, vvv
    - Order product, cancel order, repay the order
    - Checkout order: support 2 payment methods
          - VNPay
          - COD (Cash on delivery)
    - Management: profile, delivery address, order
- Admin:
    - Auth: Signin  
    - Order management: read, update order status, delete, search, filter order
    - Category management: create, read, update, delete, search, filter category
    - Product management: create, read, update, delete, search, filter product
    - User management: view, lock user account, search, filter user
    - Statistic: revenue statistic, new user statistic, order statistic
- More features:
    - Integrate with GHN delivery system 

## Built With

- [Spring Boot](https://spring.io/projects/spring-boot) - The web framework used
- [Spring Security](https://docs.spring.io/spring-security/reference/index.html) - The web security used
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/reference/#auditing.annotations) 
- [Spring Mail](https://docs.spring.io/spring-framework/reference/integration/email.html) - The email sending platform used
- [Hinernate](https://hibernate.org/orm/documentation/6.4/) - The Object-Relational Mapping (ORM) platform used
- [MySQL](https://dev.mysql.com/doc/mysql-getting-started/en/) - The Database Management System (DBMS) used
- [Maven](https://maven.apache.org/) - Dependency management
- [Swagger](https://swagger.io/) - API documentation
- [Cloudinary](https://cloudinary.com/documentation) - Photo and video hosting platform

## Authors

- Chau Chhin Hua - [Linkedin](https://www.linkedin.com/in/chhin-hua/)

## Project management

- ![jira](https://github.com/chhinhua/duck-shop/assets/86371524/4e35533e-af74-47d4-ab7d-770b6765b483)
[Jira Software project](https://chhinhua02.atlassian.net/jira/software/projects/SCRUM/boards/1)
- ![github](https://github.com/chhinhua/duck-shop/assets/86371524/cb7e8a0d-4101-4708-a1dc-450117a04dc5)
 [Github project](https://github.com/users/chhinhua/projects/4)


   

