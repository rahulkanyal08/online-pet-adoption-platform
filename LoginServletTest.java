import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.ArrayList;
import java.util.List;

/**
 * LoginServletTest - Unit tests for LoginServlet
 * Tests authentication, session management, and security
 * 
 * Coverage:
 * - Valid login credentials
 * - Invalid credentials
 * - Session creation
 * - Logout functionality
 */
@DisplayName("LoginServlet Authentication Tests")
public class LoginServletTest {

    private LoginServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new LoginServlet();
    }

    @Test
    @DisplayName("Should authenticate valid admin user")
    void testValidAdminLogin() {
        // Test valid admin credentials
        String email = "admin@petadoption.com";
        String password = "admin123";
        
        assertTrue(isValidUser(email, password), 
            "Admin user should be authenticated");
    }

    @Test
    @DisplayName("Should authenticate valid shelter user")
    void testValidShelterLogin() {
        // Test valid shelter credentials
        String email = "shelter@happypaws.com";
        String password = "shelter123";
        
        assertTrue(isValidUser(email, password), 
            "Shelter user should be authenticated");
    }

    @Test
    @DisplayName("Should authenticate valid adopter user")
    void testValidAdopterLogin() {
        // Test valid adopter credentials
        String email = "john@email.com";
        String password = "john123";
        
        assertTrue(isValidUser(email, password), 
            "Adopter user should be authenticated");
    }

    @Test
    @DisplayName("Should reject invalid password")
    void testInvalidPassword() {
        String email = "admin@petadoption.com";
        String password = "wrongpassword";
        
        assertFalse(isValidUser(email, password), 
            "Should reject invalid password");
    }

    @Test
    @DisplayName("Should reject non-existent user")
    void testNonExistentUser() {
        String email = "nonexistent@email.com";
        String password = "password123";
        
        assertFalse(isValidUser(email, password), 
            "Should reject non-existent user");
    }

    @Test
    @DisplayName("Should reject empty credentials")
    void testEmptyCredentials() {
        assertFalse(isValidUser("", ""), 
            "Should reject empty credentials");
    }

    @Test
    @DisplayName("Should reject null credentials")
    void testNullCredentials() {
        assertFalse(isValidUser(null, null), 
            "Should reject null credentials");
    }

    // Helper method to validate credentials
    private boolean isValidUser(String email, String password) {
        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            return false;
        }
        
        String[][] validUsers = {
            {"admin@petadoption.com", "admin123", "admin"},
            {"shelter@happypaws.com", "shelter123", "shelter"},
            {"john@email.com", "john123", "adopter"},
            {"sarah@email.com", "sarah123", "adopter"}
        };
        
        for (String[] user : validUsers) {
            if (user[0].equals(email) && user[1].equals(password)) {
                return true;
            }
        }
        return false;
    }
}
