README

Here is a detailed README that you can use for your project. It covers setup instructions, API documentation, entity relationships, and additional features.
________________________________________
Project Name: E-commerce System
Table of Contents
1.	Project Overview
2.	Technology Used
3.	Setup Instructions
4.	Database Configurations
5.	API Documentation
6.	Entity Relationships
7.	Authentication and Authorization
8.	External Integrations
9.	Additional Features
10.	Contributing
________________________________________
Project Overview
This e-commerce system allows users to manage categories, products, and orders, with features for both admins and shoppers. The system supports product management, order processing, user authentication, role-based access control, and email notifications. The backend is built with Spring Boot, and the frontend uses Thymeleaf for dynamic page rendering.
________________________________________
Technologies Used
•	Frontend: Thymeleaf (for rendering dynamic pages), HTML, CSS, JavaScript
•	Backend: Spring Boot (Java)
•	Database: PostgreSQL (Relational Database)
•	Authentication: Spring Security with JWT
•	Email Notifications: Spring Mail (for sending email notifications)
________________________________________
Setup Instructions
Prerequisites
Before you start, make sure you have the following installed:
•	JDK 21 or higher: For building the Spring Boot application
•	Maven: For dependency management and building the project
•	PostgreSQL: For the database
•	IDE (IntelliJ IDEA, Eclipse, etc.) for coding and debugging
1. Clone the Repository
Clone the repository to your local machine using Git:
git clone https://github.com/yourusername/ecommerce-system.git
2. Configure Database
Ensure that PostgreSQL is running on your machine. Create a new database for the project:
CREATE DATABASE ecommerce;
You will need to update the database credentials in the application.properties file:
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
3. Build and Run the Application
Navigate to the root directory of the project and use Maven to build and run the application:
mvn clean install
mvn spring-boot:run
Once the application is running, you can access the backend API at http://localhost:8080.
________________________________________
Database Configuration
The database is managed through PostgreSQL, and the project uses Spring Data JPA for persistence.
Entity Classes
Users Entity
Stores user data, including authentication information.
@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "username")
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private RoleEnum roles;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "email_verification_token")
    private String emailVerificationToken;

    @Column(name = "jwt_secret_key")
    private String jwtSecretKey;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "users")
    private List<Orders> orders;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "users")
    private List<Reviews> reviews;
}
Products Entity
Stores product data, including name, description, price, category, etc.
@Entity
@Table(name = "products")
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "sku")
    private String sku;

    @Column(name = "description")
    private String description;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "units_in_stock")
    private BigDecimal unitsInStock;

    @Column(name = "tags")
    private String tags;

    @CreationTimestamp
    @Column(name = "date_created",nullable = false, updatable = false)
    private Date dateCreated;

    @UpdateTimestamp
    @Column(name = "last_updated")
    private Date lastUpdated;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private Categories categories;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "products")
    private List<Reviews> reviews;

    @ManyToMany
    @JoinTable(
            name = "order_products",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "order_id")
    )
    private List<Products> products = new ArrayList<>();;
    @Column(name = "featured")
    private Boolean featured;
}
Orders Entity
Stores order data, including order status and related user.
@Entity
@Table(name = "orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "order_tracking_number")
    private String orderTrackingNumber;

    @Column(name = "total_quantity")
    private int totalQuantity;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatusEnum status;

    @Column(name = "date_created")
    @CreationTimestamp
    private Date dateCreated;

    @Column(name = "last_updated")
    @UpdateTimestamp
    private Date lastUpdated;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private Users users;

    @ManyToMany
    @JoinTable(
            name = "order_products",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Products> products = new ArrayList<>();
}
Categories Entity
Stores product categories.
@Entity
@Table(name = "categories")
public class Categories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "category_name")
    private String categoryName;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "categories")
    private List<Products> products;
}
Reviews Entity
Stores product reviews.
@Entity
@Table(name = "reviews")
public class Reviews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private Users users;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private Products products;

    @Column(name = "rating")
    private int rating;

    @Column(name = "comment")
    private String comment;

    @CreationTimestamp
    @Column(name = "date_created")
    private Date dateCreated;

    @UpdateTimestamp
    @Column(name = "last_updated")
    private Date lastUpdated;
________________________________________
API Documentation
The backend exposes RESTful API endpoints for various operations such as managing products, orders, users, and reviews.
1. User Endpoints
•	POST /register - Register a new user. 
o	Body: { "username": "user1", "email": "user1@example.com", "password": "password", “firstName”: “John”, ”lastName”:”Doe”, ”dob”:”YYYY-MM-DD”,  “phoneNumber”  : “1234567890”}
•	POST /login - Log in a user and get a JWT token. 
o	Body: { "username": "user1", "password": "password" }
o	Response: { "token": "JWT_TOKEN" }
2. Product Endpoints
•	GET /viewProductsPage - Get the project management page
•	GET /list - Fetches all products from the database.
•	POST /registerProducts - Add a new product (Admin only). 
•	POST products/toggleFeatured - Toggles a product's "featured" status.
•	GET /shop - Displays the shop page with all available products and categories, including the user ID extracted from JWT cookies.
•	POST /productDetails - Displays detailed information about a specific product.
•	POST /prepareOrder - Prepares the page to place an order for a specific product.
•	POST /placeOrder - Places an order for a product.
•	GET /category/{categoryName} - Displays products filtered by category.
•	GET /tag/{tag} -Displays products filtered by a tag.
•	GET /search - Searches for products based on a query string.
•	GET /viewProducts - Fetches all products from the database.
•	GET /getProductById/{productId} - Fetches a specific product by its ID.
•	POST /update-products - Shows the product update form for a specific product.
•	POST /update-product - Updates an existing product's details.
•	POST /delete-product - Deletes a product by its ID.
3. Order Endpoints
•	GET /user/orders - This endpoint displays the user's pending orders.
•	GET /user/order-history - Displays the user's order history (all orders, regardless of status). 
•	POST /order-details - Shows the details of a specific order.
•	GET /viewOrderManagement - Displays the order management page for admin to view all orders.
•	POST /update-order - Updates an order's details.
•	POST /update-orders - Displays a form to update a specific order.
•	POST /delete-order - Deletes an order by its ID.
•	SCHEDULED TASK /scheduled/sendDailyOrderStatusNotifications - Sends a daily email notification to users with pending orders.
4. Review Endpoints
•	POST /submit - This endpoint allows a user to submit a new review for a product.
o	Body: {"id": 1, "userId": 1,"productId": 101,"rating": 4, "comment": "Great product!"}
•	PUT /update/{reviewId} - This endpoint allows an existing review to be updated.
•	GET /product/{productId} - This endpoint retrieves all reviews for a specific product, identified by its productId.
5. Category Endpoints
•	GET /viewCategoryPage - This endpoint is responsible for rendering the category page where users can view all existing categories and add a new category.
•	GET /viewCategories - This endpoint returns a list of all categories in JSON format.
•	POST /registerCategory - This endpoint handles the registration of a new category.
•	POST /update-category - This endpoint is used for updating an existing category.
•	POST /update-categories - This endpoint is used for showing the update form for an existing category.
•	POST /delete-category - This endpoint handles the deletion of a category.

________________________________________
Entity Relationships
•	Users to Orders: One-to-Many (One user can have many orders).
•	Users to Reviews: One-to-Many (One user can write many reviews).
•	Products to Orders: Many-to-Many (Products can belong to many orders, and orders can have many products).
•	Products to Categories: Many-to-One (Each product belongs to one category).
•	Products to Reviews: One-to-Many (Each product can have many reviews).
•	Categories to Products: One-to-Many (Each category can have many products).
________________________________________
Authentication & Authorization
The system uses Spring Security with JWT for authentication and authorization.
JWT Authentication Flow:
1.	User logs in with their credentials (username and password).
2.	Spring Security validates the credentials and returns a JWT token.
3.	The token is sent with subsequent requests in the Authorization header.
________________________________________
External Integrations
Email Notifications
•	Spring Mail is used to send email notifications to users for actions like order confirmation, registration success, etc.
•	Configure SMTP server settings in application.properties:
spring.mail.host=smtp.example.com
spring.mail.port=587
spring.mail.username=your_email@example.com
spring.mail.password=your_password
________________________________________
Additional Features
•	Role-based Access Control: The system supports multiple roles such as ADMIN and USER, allowing restricted access to certain endpoints.
•	Product Search: Users can search for products using filters like price range, category, and keywords.
•	Order Tracking: Admins can track the status of orders and update their statuses (e.g., pending, shipped, delivered).
________________________________________
Contributing
Contributions are welcome! Please fork the repository, create a new branch, and submit a pull request with your changes.
________________________________________
This README provides detailed information about setting up and working with the e-commerce system, including how to run the backend, the API documentation, database entities, and more.

