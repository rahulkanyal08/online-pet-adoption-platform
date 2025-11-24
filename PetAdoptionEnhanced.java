import java.util.*;
import java.util.concurrent.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.*;

// ===================== CUSTOM EXCEPTIONS =====================
class InvalidUserException extends Exception {
    public InvalidUserException(String message) {
        super(message);
    }
}

class PetNotFoundException extends Exception {
    public PetNotFoundException(String message) {
        super(message);
    }
}

class ApplicationException extends Exception {
    public ApplicationException(String message) {
        super(message);
    }
}

class DatabaseException extends Exception {
    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}

// ===================== INTERFACES (POLYMORPHISM) =====================

/**
 * Interface for database persistence
 */
interface Persistable {
    void save() throws DatabaseException;

    void delete() throws DatabaseException;

    void update() throws DatabaseException;
}

/**
 * Interface for searchable entities
 */
interface Searchable<T> {
    List<T> search(String keyword) throws DatabaseException;

    List<T> findAll() throws DatabaseException;
}

/**
 * Interface for authentication
 */
interface Authenticatable {
    boolean authenticate(String email, String password) throws InvalidUserException;

    String getRole();
}

// ===================== BASE CLASS (INHERITANCE) =====================

/**
 * Abstract base class for all entities
 * Demonstrates inheritance and abstraction
 */
abstract class Entity implements Persistable {
    protected int id;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    public Entity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Entity(int id) {
        this.id = id;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Abstract methods - polymorphism
    abstract void validate() throws Exception;

    abstract String getEntityType();

    @Override
    public String toString() {
        return String.format("[%s] ID: %d | Created: %s | Updated: %s",
                getEntityType(), id,
                createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                updatedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }
}

// ===================== MODEL CLASSES (INHERITANCE FROM ENTITY)
// =====================

/**
 * User class - inherits from Entity, implements Authenticatable
 */
class User extends Entity implements Authenticatable {
    private String name;
    private String email;
    private String role; // admin, shelter, adopter
    private String password;

    public User(int id, String name, String email, String role, String password) {
        super(id);
        this.name = name;
        this.email = email;
        this.role = role;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getRole() {
        return role;
    }

    @Override
    public boolean authenticate(String email, String password) throws InvalidUserException {
        if (!this.email.equals(email) || !this.password.equals(password)) {
            throw new InvalidUserException("Invalid email or password!");
        }
        return true;
    }

    @Override
    void validate() throws InvalidUserException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidUserException("User name cannot be empty!");
        }
        if (email == null || !email.contains("@")) {
            throw new InvalidUserException("Invalid email format!");
        }
        if (password == null || password.length() < 4) {
            throw new InvalidUserException("Password must be at least 4 characters!");
        }
    }

    @Override
    String getEntityType() {
        return "User";
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Name: %s | Email: %s | Role: %s", name, email, role);
    }

    @Override
    public void save() throws DatabaseException {
        // Will be implemented in DAO
    }

    @Override
    public void delete() throws DatabaseException {
        // Will be implemented in DAO
    }

    @Override
    public void update() throws DatabaseException {
        // Will be implemented in DAO
    }
}

/**
 * Pet class - inherits from Entity, implements Searchable
 */
class Pet extends Entity implements Searchable<Pet> {
    private int shelterId;
    private String name;
    private String type;
    private String breed;
    private int age;
    private String description;
    private String adoptionStatus;
    private String approvalStatus;

    public Pet(int id, int shelterId, String name, String type, String breed, int age, String description) {
        super(id);
        this.shelterId = shelterId;
        this.name = name;
        this.type = type;
        this.breed = breed;
        this.age = age;
        this.description = description;
        this.adoptionStatus = "available";
        this.approvalStatus = "pending";
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getBreed() {
        return breed;
    }

    public String getAdoptionStatus() {
        return adoptionStatus;
    }

    public void setAdoptionStatus(String status) {
        this.adoptionStatus = status;
    }

    @Override
    void validate() throws PetNotFoundException {
        if (name == null || name.trim().isEmpty()) {
            throw new PetNotFoundException("Pet name cannot be empty!");
        }
        if (age < 0 || age > 50) {
            throw new PetNotFoundException("Invalid pet age!");
        }
    }

    @Override
    String getEntityType() {
        return "Pet";
    }

    @Override
    public List<Pet> search(String keyword) throws DatabaseException {
        // Will be implemented in DAO
        return new ArrayList<>();
    }

    @Override
    public List<Pet> findAll() throws DatabaseException {
        // Will be implemented in DAO
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Name: %s | Type: %s | Breed: %s | Age: %d | Status: %s",
                name, type, breed, age, adoptionStatus);
    }

    @Override
    public void save() throws DatabaseException {
        // Will be implemented in DAO
    }

    @Override
    public void delete() throws DatabaseException {
        // Will be implemented in DAO
    }

    @Override
    public void update() throws DatabaseException {
        // Will be implemented in DAO
    }
}

/**
 * Application class - inherits from Entity
 */
class Application extends Entity {
    private int adopterId;
    private int petId;
    private String status;
    private String applicationNotes;

    public Application(int id, int adopterId, int petId, String applicationNotes) {
        super(id);
        this.adopterId = adopterId;
        this.petId = petId;
        this.applicationNotes = applicationNotes;
        this.status = "submitted";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    void validate() throws ApplicationException {
        if (adopterId <= 0 || petId <= 0) {
            throw new ApplicationException("Invalid adopter or pet ID!");
        }
    }

    @Override
    String getEntityType() {
        return "Application";
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Adopter: %d | Pet: %d | Status: %s",
                adopterId, petId, status);
    }

    @Override
    public void save() throws DatabaseException {
        // Will be implemented in DAO
    }

    @Override
    public void delete() throws DatabaseException {
        // Will be implemented in DAO
    }

    @Override
    public void update() throws DatabaseException {
        // Will be implemented in DAO
    }
}

// ===================== DATABASE CONNECTION (JDBC) =====================

/**
 * Database connection manager
 * Handles JDBC connections to MySQL
 */
class DatabaseConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/pet_adoption";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    private static Connection connection;

    static {
        try {
            Class.forName(DRIVER);
            System.out.println("✓ JDBC Driver loaded successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ JDBC Driver not found: " + e.getMessage());
        }
    }

    /**
     * Get database connection
     */
    public static Connection getConnection() throws DatabaseException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("✓ Database connected successfully!");
            }
            return connection;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to connect to database: " + e.getMessage(), e);
        }
    }

    /**
     * Close database connection
     */
    public static void closeConnection() throws DatabaseException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Database connection closed!");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to close connection: " + e.getMessage(), e);
        }
    }

    /**
     * Check if database exists, create if needed
     */
    public static void initializeDatabase() throws DatabaseException {
        try {
            // Connect without specifying database
            String url = "jdbc:mysql://localhost:3306";
            Connection tempConn = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
            Statement stmt = tempConn.createStatement();

            // Create database
            stmt.execute("CREATE DATABASE IF NOT EXISTS pet_adoption");
            System.out.println("✓ Database 'pet_adoption' ready!");

            stmt.close();
            tempConn.close();

            // Now create tables
            createTables();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to initialize database: " + e.getMessage(), e);
        }
    }

    /**
     * Create all required tables
     */
    private static void createTables() throws DatabaseException {
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();

            // Users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "name VARCHAR(100) NOT NULL," +
                    "email VARCHAR(100) UNIQUE NOT NULL," +
                    "role VARCHAR(20) NOT NULL," +
                    "password VARCHAR(100) NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                    ")");
            System.out.println("✓ Table 'users' created/verified!");

            // Pets table
            stmt.execute("CREATE TABLE IF NOT EXISTS pets (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "shelter_id INT NOT NULL," +
                    "name VARCHAR(100) NOT NULL," +
                    "type VARCHAR(50) NOT NULL," +
                    "breed VARCHAR(100) NOT NULL," +
                    "age INT NOT NULL," +
                    "description TEXT," +
                    "adoption_status VARCHAR(20) DEFAULT 'available'," +
                    "approval_status VARCHAR(20) DEFAULT 'pending'," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                    ")");
            System.out.println("✓ Table 'pets' created/verified!");

            // Applications table
            stmt.execute("CREATE TABLE IF NOT EXISTS applications (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "adopter_id INT NOT NULL," +
                    "pet_id INT NOT NULL," +
                    "status VARCHAR(20) DEFAULT 'submitted'," +
                    "application_notes TEXT," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (adopter_id) REFERENCES users(id)," +
                    "FOREIGN KEY (pet_id) REFERENCES pets(id)" +
                    ")");
            System.out.println("✓ Table 'applications' created/verified!");

            // Messages table
            stmt.execute("CREATE TABLE IF NOT EXISTS messages (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "sender_id INT NOT NULL," +
                    "recipient_id INT NOT NULL," +
                    "content TEXT NOT NULL," +
                    "sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (sender_id) REFERENCES users(id)," +
                    "FOREIGN KEY (recipient_id) REFERENCES users(id)" +
                    ")");
            System.out.println("✓ Table 'messages' created/verified!");

            stmt.close();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create tables: " + e.getMessage(), e);
        }
    }
}

// ===================== BASE DAO CLASS =====================

/**
 * Abstract base DAO class with generic type
 * Demonstrates generics and abstraction
 */
abstract class BaseDAO<T extends Entity> {
    protected Class<T> entityClass;

    public BaseDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    abstract void save(T entity) throws DatabaseException;

    abstract T findById(int id) throws DatabaseException;

    abstract List<T> findAll() throws DatabaseException;

    abstract void update(T entity) throws DatabaseException;

    abstract void delete(int id) throws DatabaseException;
}

// ===================== USER DAO (DATABASE OPERATIONS) =====================

/**
 * UserDAO - Data Access Object for User entities
 * Implements CRUD operations with JDBC
 */
class UserDAO extends BaseDAO<User> {
    private static final String TABLE_NAME = "users";

    public UserDAO() {
        super(User.class);
    }

    @Override
    void save(User user) throws DatabaseException {
        String sql = "INSERT INTO " + TABLE_NAME + " (name, email, role, password) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getPassword());
            pstmt.executeUpdate();
            System.out.println("✓ User saved successfully!");
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save user: " + e.getMessage(), e);
        }
    }

    @Override
    User findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role"),
                        rs.getString("password"));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find user: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    List<User> findAll() throws DatabaseException {
        List<User> users = new CopyOnWriteArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME;
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role"),
                        rs.getString("password")));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch users: " + e.getMessage(), e);
        }
        return users;
    }

    @Override
    void update(User user) throws DatabaseException {
        String sql = "UPDATE " + TABLE_NAME + " SET name = ?, email = ?, role = ?, password = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getPassword());
            pstmt.setInt(5, user.getId());
            pstmt.executeUpdate();
            System.out.println("✓ User updated successfully!");
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update user: " + e.getMessage(), e);
        }
    }

    @Override
    void delete(int id) throws DatabaseException {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("✓ User deleted successfully!");
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete user: " + e.getMessage(), e);
        }
    }

    /**
     * Find user by email (for login)
     */
    User findByEmail(String email) throws DatabaseException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role"),
                        rs.getString("password"));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find user by email: " + e.getMessage(), e);
        }
        return null;
    }
}

// ===================== PET DAO =====================

/**
 * PetDAO - Data Access Object for Pet entities
 */
class PetDAO extends BaseDAO<Pet> {
    private static final String TABLE_NAME = "pets";

    public PetDAO() {
        super(Pet.class);
    }

    @Override
    void save(Pet pet) throws DatabaseException {
        String sql = "INSERT INTO " + TABLE_NAME
                + " (shelter_id, name, type, breed, age, description, adoption_status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, pet.getId());
            pstmt.setString(2, pet.getName());
            pstmt.setString(3, pet.getType());
            pstmt.setString(4, pet.getBreed());
            pstmt.setInt(5, pet.getId());
            pstmt.setString(6, "");
            pstmt.setString(7, pet.getAdoptionStatus());
            pstmt.executeUpdate();
            System.out.println("✓ Pet saved successfully!");
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save pet: " + e.getMessage(), e);
        }
    }

    @Override
    Pet findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Pet(
                        rs.getInt("id"),
                        rs.getInt("shelter_id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getString("breed"),
                        rs.getInt("age"),
                        rs.getString("description"));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find pet: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    List<Pet> findAll() throws DatabaseException {
        List<Pet> pets = new CopyOnWriteArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME;
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                pets.add(new Pet(
                        rs.getInt("id"),
                        rs.getInt("shelter_id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getString("breed"),
                        rs.getInt("age"),
                        rs.getString("description")));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch pets: " + e.getMessage(), e);
        }
        return pets;
    }

    @Override
    void update(Pet pet) throws DatabaseException {
        String sql = "UPDATE " + TABLE_NAME
                + " SET name = ?, type = ?, breed = ?, age = ?, adoption_status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pet.getName());
            pstmt.setString(2, pet.getType());
            pstmt.setString(3, pet.getBreed());
            pstmt.setInt(4, pet.getId());
            pstmt.setString(5, pet.getAdoptionStatus());
            pstmt.setInt(6, pet.getId());
            pstmt.executeUpdate();
            System.out.println("✓ Pet updated successfully!");
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update pet: " + e.getMessage(), e);
        }
    }

    @Override
    void delete(int id) throws DatabaseException {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("✓ Pet deleted successfully!");
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete pet: " + e.getMessage(), e);
        }
    }

    /**
     * Find available pets (polymorphism - overriding method behavior)
     */
    List<Pet> findAvailable() throws DatabaseException {
        List<Pet> pets = new CopyOnWriteArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE adoption_status = 'available'";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                pets.add(new Pet(
                        rs.getInt("id"),
                        rs.getInt("shelter_id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getString("breed"),
                        rs.getInt("age"),
                        rs.getString("description")));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch available pets: " + e.getMessage(), e);
        }
        return pets;
    }
}

// ===================== APPLICATION DAO =====================

/**
 * ApplicationDAO - Data Access Object for Application entities
 */
class ApplicationDAO extends BaseDAO<Application> {
    private static final String TABLE_NAME = "applications";

    public ApplicationDAO() {
        super(Application.class);
    }

    @Override
    void save(Application application) throws DatabaseException {
        String sql = "INSERT INTO " + TABLE_NAME
                + " (adopter_id, pet_id, status, application_notes) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, application.getId());
            pstmt.setInt(2, application.getId());
            pstmt.setString(3, application.getStatus());
            pstmt.setString(4, "");
            pstmt.executeUpdate();
            System.out.println("✓ Application saved successfully!");
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save application: " + e.getMessage(), e);
        }
    }

    @Override
    Application findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Application(
                        rs.getInt("id"),
                        rs.getInt("adopter_id"),
                        rs.getInt("pet_id"),
                        rs.getString("application_notes"));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find application: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    List<Application> findAll() throws DatabaseException {
        List<Application> applications = new CopyOnWriteArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME;
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                applications.add(new Application(
                        rs.getInt("id"),
                        rs.getInt("adopter_id"),
                        rs.getInt("pet_id"),
                        rs.getString("application_notes")));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch applications: " + e.getMessage(), e);
        }
        return applications;
    }

    @Override
    void update(Application application) throws DatabaseException {
        String sql = "UPDATE " + TABLE_NAME + " SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, application.getStatus());
            pstmt.setInt(2, application.getId());
            pstmt.executeUpdate();
            System.out.println("✓ Application updated successfully!");
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update application: " + e.getMessage(), e);
        }
    }

    @Override
    void delete(int id) throws DatabaseException {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("✓ Application deleted successfully!");
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete application: " + e.getMessage(), e);
        }
    }
}

// ===================== MULTITHREADING - DATA BACKUP TASK =====================

/**
 * DataBackupTask - Demonstrates multithreading
 * Runs as a background thread to periodically backup data
 */
class DataBackupTask implements Runnable {
    private volatile boolean running = true;
    private final long BACKUP_INTERVAL = 30000; // 30 seconds for demo

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        System.out.println("\n🔄 Data Backup Task started (running every 30 seconds)...\n");
        while (running) {
            try {
                Thread.sleep(BACKUP_INTERVAL);
                performBackup();
            } catch (InterruptedException e) {
                System.out.println("Backup task interrupted: " + e.getMessage());
                break;
            }
        }
        System.out.println("🛑 Data Backup Task stopped!\n");
    }

    /**
     * Synchronized method - demonstrates synchronization
     */
    private synchronized void performBackup() {
        try {
            System.out.println("[BACKUP] Backing up database at "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

            // Simulate backup operation
            UserDAO userDAO = new UserDAO();
            PetDAO petDAO = new PetDAO();

            List<User> users = userDAO.findAll();
            List<Pet> pets = petDAO.findAll();

            System.out.println("[BACKUP] ✓ Users backed up: " + users.size());
            System.out.println("[BACKUP] ✓ Pets backed up: " + pets.size());
        } catch (DatabaseException e) {
            System.err.println("[BACKUP] Error during backup: " + e.getMessage());
        }
    }
}

// ===================== MAIN APPLICATION =====================

/**
 * PetAdoptionEnhanced - Main application with all OOP features
 * 
 * Features:
 * ✅ OOP Inheritance: Entity base class with User, Pet, Application extending it
 * ✅ Polymorphism: Multiple implementations of Persistable interface
 * ✅ Interfaces: Persistable, Searchable, Authenticatable
 * ✅ Exception Handling: Custom exceptions (InvalidUserException,
 * PetNotFoundException, etc.)
 * ✅ Collections: CopyOnWriteArrayList for thread-safe collections
 * ✅ Generics: BaseDAO<T extends Entity> with generic type parameters
 * ✅ Multithreading: DataBackupTask implements Runnable with synchronized
 * methods
 * ✅ JDBC: Full database connectivity with MySQL
 * ✅ DAO Pattern: UserDAO, PetDAO, ApplicationDAO classes
 */
public class PetAdoptionEnhanced {
    private static DataBackupTask backupTask;
    private static Thread backupThread;

    public static void main(String[] args) {
        try {
            System.out.println("╔════════════════════════════════════════════════════════╗");
            System.out.println("║  Pet Adoption Platform - Enhanced with OOP Features    ║");
            System.out.println("║  Includes: JDBC, DAO, Multithreading, Generics        ║");
            System.out.println("╚════════════════════════════════════════════════════════╝\n");

            // Initialize database and create tables
            System.out.println("📊 Initializing Database...");
            DatabaseConnection.initializeDatabase();

            // Start background backup task
            System.out.println("\n🚀 Starting Backup Thread...");
            backupTask = new DataBackupTask();
            backupThread = new Thread(backupTask, "DataBackupThread");
            backupThread.setDaemon(true);
            backupThread.start();

            // Demo operations
            System.out.println("\n📝 Demo: Creating and saving users...");
            demonstrateDAOOperations();

            // Keep application running for demo
            Thread.sleep(5000);

            // Stop backup thread
            backupTask.stop();
            backupThread.join();

            // Close database
            DatabaseConnection.closeConnection();

            System.out.println("\n✅ Application completed successfully!");

        } catch (Exception e) {
            System.err.println("❌ Application error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Demonstrate DAO operations and polymorphism
     */
    private static void demonstrateDAOOperations() throws DatabaseException {
        // Create DAOs (polymorphic objects)
        UserDAO userDAO = new UserDAO();
        PetDAO petDAO = new PetDAO();
        ApplicationDAO appDAO = new ApplicationDAO();

        // Create and save users (demonstrating inheritance and polymorphism)
        User admin = new User(1, "Admin User", "admin@pet.com", "admin", "admin123");
        User shelter = new User(2, "Shelter Manager", "shelter@pet.com", "shelter", "shelter123");
        User adopter = new User(3, "John Adopter", "adopter@pet.com", "adopter", "adopter123");

        System.out.println("Saving users to database...");
        userDAO.save(admin);
        userDAO.save(shelter);
        userDAO.save(adopter);

        // Retrieve and display users
        System.out.println("\n📋 All Users in Database:");
        List<User> users = userDAO.findAll();
        for (User user : users) {
            System.out.println("  " + user);
        }

        // Create and save pets
        Pet pet1 = new Pet(1, 2, "Max", "Dog", "Golden Retriever", 3, "Friendly and playful");
        Pet pet2 = new Pet(2, 2, "Whiskers", "Cat", "Persian", 2, "Calm and cuddly");

        System.out.println("\nSaving pets to database...");
        petDAO.save(pet1);
        petDAO.save(pet2);

        // Retrieve available pets
        System.out.println("\n🐾 Available Pets:");
        List<Pet> availablePets = petDAO.findAvailable();
        for (Pet pet : availablePets) {
            System.out.println("  " + pet);
        }

        System.out.println("\n✓ DAO Operations Demo Complete!");
    }
}
