# E-Shopping Zone Backend

`EShoppingZone` is a backend microservices application built with Spring Boot. This project serves as the foundation for an e-commerce platform, providing various services like product management, user authentication, and order processing. The services are designed to be independent and scalable, communicating with each other through a service discovery mechanism.

## 🚀 Technologies

* **Spring Boot**: The core framework for building the application.
* **Spring Cloud**: Provides a suite of tools for building distributed systems, including:
    * **Eureka Server**: For service discovery, allowing microservices to find and communicate with each other.
    * **Spring Cloud Gateway**: Acts as a single entry point for all API calls to the microservices.
* **MySQL**: The relational database used to persist all application data.
* **Maven**: The build automation and dependency management tool.

---

## 🛠️ Prerequisites

Before you begin, ensure you have the following installed on your machine:

* **Java 17 or higher**: The required Java Development Kit (JDK) for the project.
* **Maven**: To build and run the Spring Boot applications.
* **MySQL Server**: The database where the application's data will be stored.
* **An IDE**: **IntelliJ IDEA** is recommended for a seamless development experience.

---

## ⚙️ Backend Setup

Follow these steps to set up and run the Spring Boot microservices.

### 1. Database Configuration

* Create a new MySQL database for the project. You can execute the following SQL command in your MySQL client:

    ```sql
    CREATE DATABASE eshopping_db;
    ```
* The application uses **JPA (Java Persistence API)** with **`ddl-auto` set to `update`**, so tables will be automatically created or updated when you run the services.

### 2. Configure `application.properties`

Navigate to the `microservice-backend` folder. Each microservice (e.g., `product-service`, `user-service`) has its own `src/main/resources/application.properties` file that needs to be configured.

* **For the Eureka Server**: The Eureka server acts as the service registry and doesn't require database configuration. Ensure its port is configured correctly.

    ```properties
    server.port=8761
    eureka.client.register-with-eureka=false
    eureka.client.fetch-registry=false
    ```

* **For all other microservices**: You must configure the database connection details.

    ```properties
    # Spring Boot Application Properties
    
    # Server configuration
    server.port=8080 # This can be a different port for each service
    
    # Eureka Client configuration
    eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
    
    # Database configuration
    spring.datasource.url=jdbc:mysql://localhost:3306/eshopping_db
    spring.datasource.username=root
    spring.datasource.password=your_password
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    ```

    * **`spring.datasource.username`**: Replace `root` with your MySQL username.
    * **`spring.datasource.password`**: Replace `your_password` with your MySQL password.
    * **`spring.datasource.url`**: Verify the database name (`eshopping_db`) and port (`3306`) are correct.

### 3. Run the Backend

You need to run the services in a specific order to ensure they can register with the Eureka server.

1.  **Start the Eureka Server**: Navigate to the Eureka server's directory in your terminal or IDE and run the application.

    ```bash
    cd /path/to/microservice-backend/eureka-server
    mvn spring-boot:run
    ```

2.  **Start other microservices**: Once the Eureka server is running, start the other microservices one by one.

    ```bash
    cd /path/to/microservice-backend/product-service
    mvn spring-boot:run
    
    # Repeat for other services like user-service, order-service, etc.
    ```
* You can verify that the services are registered by navigating to the Eureka dashboard at `http://localhost:8761`.

---

## 🤝 Contribution

Contributions are welcome! If you find a bug or have a suggestion, please open an issue or a pull request.
