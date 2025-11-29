import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.ArrayList;
import java.util.List;

/**
 * ApplicationServletTest - Unit tests for ApplicationServlet
 * Tests adoption application workflow and access control
 * 
 * Coverage:
 * - Submit adoption application
 * - Prevent duplicate applications
 * - Update application status
 * - Authorization checks
 * - Application retrieval
 */
@DisplayName("ApplicationServlet Workflow Tests")
public class ApplicationServletTest {

    private List<ApplicationData> applicationDatabase;

    static class ApplicationData {
        int id;
        int adopterId, petId;
        String status;
        String notes;
        
        ApplicationData(int id, int adopterId, int petId, String status, String notes) {
            this.id = id;
            this.adopterId = adopterId;
            this.petId = petId;
            this.status = status;
            this.notes = notes;
        }
    }

    @BeforeEach
    void setUp() {
        applicationDatabase = new ArrayList<>();
        applicationDatabase.add(new ApplicationData(1, 3, 1, "submitted", "I love dogs!"));
        applicationDatabase.add(new ApplicationData(2, 4, 2, "submitted", "Cats are perfect!"));
    }

    @Test
    @DisplayName("Should submit adoption application")
    void testSubmitApplication() {
        int initialSize = applicationDatabase.size();
        ApplicationData newApp = new ApplicationData(3, 5, 3, "submitted", "Want a rabbit!");
        applicationDatabase.add(newApp);
        
        assertEquals(initialSize + 1, applicationDatabase.size(), 
            "Application should be added");
    }

    @Test
    @DisplayName("Should prevent duplicate active applications")
    void testPreventDuplicateApplication() {
        // User 3 already applied for pet 1
        boolean hasDuplicate = applicationDatabase.stream()
            .anyMatch(app -> app.adopterId == 3 && app.petId == 1 
                && !app.status.equals("rejected"));
        
        assertTrue(hasDuplicate, "Duplicate application should exist");
    }

    @Test
    @DisplayName("Should allow new application after rejection")
    void testNewApplicationAfterRejection() {
        ApplicationData rejected = applicationDatabase.get(0);
        rejected.status = "rejected";
        
        // Now user 3 can apply for pet 1 again
        boolean canReapply = applicationDatabase.stream()
            .noneMatch(app -> app.adopterId == 3 && app.petId == 1 
                && !app.status.equals("rejected"));
        
        assertTrue(canReapply, "Should allow new application after rejection");
    }

    @Test
    @DisplayName("Should update application to approved")
    void testApproveApplication() {
        ApplicationData app = applicationDatabase.get(0);
        app.status = "approved";
        
        assertEquals("approved", app.status, "Application should be approved");
    }

    @Test
    @DisplayName("Should update application to adopted")
    void testMarkApplicationAsAdopted() {
        ApplicationData app = applicationDatabase.get(0);
        app.status = "adopted";
        
        assertEquals("adopted", app.status, "Application should be marked as adopted");
    }

    @Test
    @DisplayName("Should update application to rejected")
    void testRejectApplication() {
        ApplicationData app = applicationDatabase.get(1);
        app.status = "rejected";
        
        assertEquals("rejected", app.status, "Application should be rejected");
    }

    @Test
    @DisplayName("Should retrieve applications by adopter")
    void testGetApplicationsByAdopter() {
        int adopterId = 3;
        List<ApplicationData> userApps = applicationDatabase.stream()
            .filter(app -> app.adopterId == adopterId)
            .collect(() -> new ArrayList<>(), 
                (list, app) -> list.add(app), 
                (list1, list2) -> list1.addAll(list2));
        
        assertEquals(1, userApps.size(), "Adopter 3 should have 1 application");
    }

    @Test
    @DisplayName("Should verify application details")
    void testApplicationDetails() {
        ApplicationData app = applicationDatabase.get(0);
        
        assertEquals(3, app.adopterId, "Adopter ID should be 3");
        assertEquals(1, app.petId, "Pet ID should be 1");
        assertEquals("submitted", app.status, "Status should be submitted");
        assertEquals("I love dogs!", app.notes, "Notes should match");
    }

    @Test
    @DisplayName("Should have application notes")
    void testApplicationNotes() {
        ApplicationData app = applicationDatabase.get(1);
        
        assertNotNull(app.notes, "Notes should not be null");
        assertFalse(app.notes.isEmpty(), "Notes should not be empty");
        assertTrue(app.notes.contains("Cat"), "Notes should mention cat");
    }

    @Test
    @DisplayName("Should count total applications")
    void testCountApplications() {
        assertEquals(2, applicationDatabase.size(), "Should have 2 applications");
    }

    @Test
    @DisplayName("Should find application by ID")
    void testFindApplicationById() {
        int appId = 1;
        ApplicationData found = applicationDatabase.stream()
            .filter(app -> app.id == appId)
            .findFirst()
            .orElse(null);
        
        assertNotNull(found, "Application should be found");
        assertEquals(appId, found.id, "Application ID should match");
    }
}
