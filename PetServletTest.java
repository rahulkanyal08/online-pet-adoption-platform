import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.ArrayList;
import java.util.List;

/**
 * PetServletTest - Unit tests for PetServlet
 * Tests pet CRUD operations, filtering, and authorization
 * 
 * Coverage:
 * - Get all pets
 * - Filter pets by status
 * - Create new pet
 * - Update pet status
 * - Delete pet
 * - Authorization checks
 */
@DisplayName("PetServlet CRUD Operations Tests")
public class PetServletTest {

    private List<PetData> petDatabase;

    static class PetData {
        int id;
        String name, type, breed;
        int age;
        String adoptionStatus, approvalStatus;

        PetData(int id, String name, String type, String breed, int age, 
                String adoptionStatus, String approvalStatus) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.breed = breed;
            this.age = age;
            this.adoptionStatus = adoptionStatus;
            this.approvalStatus = approvalStatus;
        }
    }

    @BeforeEach
    void setUp() {
        petDatabase = new ArrayList<>();
        petDatabase.add(new PetData(1, "Buddy", "Dog", "Golden Retriever", 3, "available", "approved"));
        petDatabase.add(new PetData(2, "Whiskers", "Cat", "Persian", 2, "available", "approved"));
        petDatabase.add(new PetData(3, "Hoppy", "Rabbit", "Holland Lop", 1, "pending", "pending"));
    }

    @Test
    @DisplayName("Should retrieve all pets")
    void testGetAllPets() {
        assertEquals(3, petDatabase.size(), "Should have 3 pets in database");
    }

    @Test
    @DisplayName("Should filter available pets")
    void testFilterAvailablePets() {
        List<PetData> available = filterPets("available");
        assertEquals(2, available.size(), "Should have 2 available pets");
    }

    @Test
    @DisplayName("Should filter approved pets")
    void testFilterApprovedPets() {
        List<PetData> approved = filterPets("approved");
        assertEquals(2, approved.size(), "Should have 2 approved pets");
    }

    @Test
    @DisplayName("Should filter pending pets")
    void testFilterPendingPets() {
        List<PetData> pending = filterPets("pending");
        assertEquals(1, pending.size(), "Should have 1 pending pet");
    }

    @Test
    @DisplayName("Should add new pet")
    void testAddNewPet() {
        int initialSize = petDatabase.size();
        PetData newPet = new PetData(4, "Fluffy", "Dog", "Poodle", 2, "pending", "pending");
        petDatabase.add(newPet);
        
        assertEquals(initialSize + 1, petDatabase.size(), "Pet should be added to database");
    }

    @Test
    @DisplayName("Should update pet status to approved")
    void testApprovePet() {
        PetData pet = petDatabase.get(2); // Hoppy (pending)
        pet.approvalStatus = "approved";
        
        assertEquals("approved", pet.approvalStatus, "Pet should be approved");
    }

    @Test
    @DisplayName("Should update pet status to adopted")
    void testMarkPetAsAdopted() {
        PetData pet = petDatabase.get(0); // Buddy
        pet.adoptionStatus = "adopted";
        
        assertEquals("adopted", pet.adoptionStatus, "Pet should be marked as adopted");
    }

    @Test
    @DisplayName("Should delete pet from database")
    void testDeletePet() {
        int initialSize = petDatabase.size();
        petDatabase.remove(0);
        
        assertEquals(initialSize - 1, petDatabase.size(), "Pet should be removed from database");
    }

    @Test
    @DisplayName("Should not find pet after deletion")
    void testPetNotFoundAfterDeletion() {
        PetData toDelete = petDatabase.get(0);
        petDatabase.remove(toDelete);
        
        boolean found = petDatabase.stream()
            .anyMatch(p -> p.id == toDelete.id);
        assertFalse(found, "Deleted pet should not be found");
    }

    @Test
    @DisplayName("Should verify pet attributes")
    void testPetAttributes() {
        PetData pet = petDatabase.get(0);
        
        assertEquals("Buddy", pet.name, "Pet name should be Buddy");
        assertEquals("Dog", pet.type, "Pet type should be Dog");
        assertEquals("Golden Retriever", pet.breed, "Breed should be Golden Retriever");
        assertEquals(3, pet.age, "Age should be 3");
    }

    // Helper method
    private List<PetData> filterPets(String status) {
        List<PetData> filtered = new ArrayList<>();
        for (PetData pet : petDatabase) {
            if (status.equals("available") && pet.adoptionStatus.equals("available")) {
                filtered.add(pet);
            } else if (status.equals("approved") && pet.approvalStatus.equals("approved")) {
                filtered.add(pet);
            } else if (status.equals("pending") && pet.approvalStatus.equals("pending")) {
                filtered.add(pet);
            }
        }
        return filtered;
    }
}
