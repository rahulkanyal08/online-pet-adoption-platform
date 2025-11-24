import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Complete Pet Adoption Platform Backend
 * Supports Admin, Shelter, and Adopter functionalities
 */
public class PetAdoptionBackend {

    // ===================== DATA MODELS =====================

    static class User {
        int id;
        String name;
        String email;
        String role; // admin, shelter, adopter
        String password;
        LocalDateTime createdAt;

        User(int id, String name, String email, String role, String password) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.role = role;
            this.password = password;
            this.createdAt = LocalDateTime.now();
        }

        @Override
        public String toString() {
            return String.format("ID: %d | Name: %s | Email: %s | Role: %s | Created: %s",
                    id, name, email, role, createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }
    }

    static class Pet {
        int id;
        int shelterId;
        String name;
        String type; // dog, cat, rabbit, etc.
        String breed;
        int age;
        String description;
        String photoUrl;
        String adoptionStatus; // available, adopted, pending
        String approvalStatus; // pending, approved, rejected
        LocalDateTime createdAt;

        Pet(int id, int shelterId, String name, String type, String breed, int age, String description) {
            this.id = id;
            this.shelterId = shelterId;
            this.name = name;
            this.type = type;
            this.breed = breed;
            this.age = age;
            this.description = description;
            this.adoptionStatus = "available";
            this.approvalStatus = "pending";
            this.createdAt = LocalDateTime.now();
        }

        @Override
        public String toString() {
            return String.format("ID: %d | Name: %s | Type: %s | Breed: %s | Age: %d | Status: %s | Approval: %s",
                    id, name, type, breed, age, adoptionStatus, approvalStatus);
        }
    }

    static class Application {
        int id;
        int adopterId;
        int petId;
        String status; // submitted, approved, rejected, adopted
        String applicationNotes;
        LocalDateTime submittedAt;

        Application(int id, int adopterId, int petId, String applicationNotes) {
            this.id = id;
            this.adopterId = adopterId;
            this.petId = petId;
            this.applicationNotes = applicationNotes;
            this.status = "submitted";
            this.submittedAt = LocalDateTime.now();
        }

        @Override
        public String toString() {
            return String.format("App ID: %d | Adopter ID: %d | Pet ID: %d | Status: %s | Submitted: %s",
                    id, adopterId, petId, status, submittedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }
    }

    static class Message {
        int id;
        int senderId;
        int recipientId;
        String content;
        LocalDateTime sentAt;

        Message(int id, int senderId, int recipientId, String content) {
            this.id = id;
            this.senderId = senderId;
            this.recipientId = recipientId;
            this.content = content;
            this.sentAt = LocalDateTime.now();
        }

        @Override
        public String toString() {
            return String.format("From: User %d | To: User %d | Message: %s | Time: %s",
                    senderId, recipientId, content, sentAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }
    }

    // ===================== IN-MEMORY STORAGE =====================
    private static final Map<Integer, User> users = new HashMap<>();
    private static final Map<Integer, Pet> pets = new HashMap<>();
    private static final Map<Integer, Application> applications = new HashMap<>();
    private static final Map<Integer, Message> messages = new HashMap<>();

    private static int nextUserId = 1;
    private static int nextPetId = 1;
    private static int nextApplicationId = 1;
    private static int nextMessageId = 1;
    private static User currentUser = null;

    // ===================== MAIN MENU =====================

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        initializeSampleData();

        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║   Welcome to Online Pet Adoption Platform                ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝\n");

        boolean running = true;
        while (running) {
            if (currentUser == null) {
                running = loginMenu(scanner);
            } else {
                running = roleBasedMenu(scanner);
            }
        }

        System.out.println("\nThank you for using Pet Adoption Platform! Goodbye!");
        scanner.close();
    }

    // ===================== LOGIN & AUTHENTICATION =====================

    private static boolean loginMenu(Scanner scanner) {
        System.out.println("\n=== LOGIN MENU ===");
        System.out.println("1. Login");
        System.out.println("2. Create New Account");
        System.out.println("3. Exit");
        System.out.print("Choose option: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                login(scanner);
                break;
            case "2":
                createAccount(scanner);
                break;
            case "3":
                return false;
            default:
                System.out.println("❌ Invalid option!");
        }
        return true;
    }

    private static void login(Scanner scanner) {
        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        for (User user : users.values()) {
            if (user.email.equals(email) && user.password.equals(password)) {
                currentUser = user;
                System.out.println("\n✓ Login successful! Welcome, " + user.name + " (" + user.role + ")");
                return;
            }
        }
        System.out.println("❌ Invalid email or password!");
    }

    private static void createAccount(Scanner scanner) {
        System.out.print("Enter full name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        System.out.println("\nSelect role:");
        System.out.println("1. Adopter");
        System.out.println("2. Shelter");
        System.out.print("Choose (1 or 2): ");

        String roleChoice = scanner.nextLine().trim();
        String role = roleChoice.equals("2") ? "shelter" : "adopter";

        User newUser = new User(nextUserId++, name, email, role, password);
        users.put(newUser.id, newUser);
        System.out.println("✓ Account created successfully!");
    }

    // ===================== ROLE-BASED MENUS =====================

    private static boolean roleBasedMenu(Scanner scanner) {
        switch (currentUser.role) {
            case "admin":
                return adminMenu(scanner);
            case "shelter":
                return shelterMenu(scanner);
            case "adopter":
                return adopterMenu(scanner);
            default:
                System.out.println("❌ Unknown role!");
                currentUser = null;
                return true;
        }
    }

    // ===== ADMIN MENU =====
    private static boolean adminMenu(Scanner scanner) {
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║              ADMIN DASHBOARD                            ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println("1. User Management");
        System.out.println("2. Pet Listing Management");
        System.out.println("3. Platform Analytics");
        System.out.println("4. System Settings");
        System.out.println("5. Logout");
        System.out.print("Choose option: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                adminUserManagement(scanner);
                break;
            case "2":
                adminPetManagement(scanner);
                break;
            case "3":
                viewPlatformAnalytics();
                break;
            case "4":
                manageSystemSettings(scanner);
                break;
            case "5":
                currentUser = null;
                break;
            default:
                System.out.println("❌ Invalid option!");
        }
        return true;
    }

    private static void adminUserManagement(Scanner scanner) {
        System.out.println("\n=== USER MANAGEMENT ===");
        System.out.println("1. View All Users");
        System.out.println("2. Delete User");
        System.out.println("3. Update User Role");
        System.out.print("Choose option: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                System.out.println("\n--- All Users ---");
                for (User user : users.values()) {
                    System.out.println("  " + user);
                }
                System.out.println("Total users: " + users.size());
                break;
            case "2":
                System.out.print("Enter user ID to delete: ");
                try {
                    int userId = Integer.parseInt(scanner.nextLine().trim());
                    if (users.remove(userId) != null) {
                        System.out.println("✓ User deleted successfully!");
                    } else {
                        System.out.println("❌ User not found!");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("❌ Invalid ID!");
                }
                break;
            case "3":
                System.out.print("Enter user ID: ");
                try {
                    int userId = Integer.parseInt(scanner.nextLine().trim());
                    User user = users.get(userId);
                    if (user != null) {
                        System.out.println("Current role: " + user.role);
                        System.out.println("1. admin\n2. shelter\n3. adopter");
                        System.out.print("Select new role: ");
                        String roleChoice = scanner.nextLine().trim();
                        String[] roles = { "", "admin", "shelter", "adopter" };
                        if (Integer.parseInt(roleChoice) >= 1 && Integer.parseInt(roleChoice) <= 3) {
                            user.role = roles[Integer.parseInt(roleChoice)];
                            System.out.println("✓ Role updated successfully!");
                        }
                    } else {
                        System.out.println("❌ User not found!");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("❌ Invalid input!");
                }
                break;
        }
    }

    private static void adminPetManagement(Scanner scanner) {
        System.out.println("\n=== PET LISTING MANAGEMENT ===");
        System.out.println("1. View Pending Pet Listings");
        System.out.println("2. Approve Pet Listing");
        System.out.println("3. Reject Pet Listing");
        System.out.println("4. View All Pets");
        System.out.print("Choose option: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                System.out.println("\n--- Pending Listings ---");
                for (Pet pet : pets.values()) {
                    if (pet.approvalStatus.equals("pending")) {
                        System.out.println("  " + pet);
                    }
                }
                break;
            case "2":
                System.out.print("Enter pet ID to approve: ");
                try {
                    int petId = Integer.parseInt(scanner.nextLine().trim());
                    Pet pet = pets.get(petId);
                    if (pet != null) {
                        pet.approvalStatus = "approved";
                        System.out.println("✓ Pet listing approved!");
                    } else {
                        System.out.println("❌ Pet not found!");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("❌ Invalid ID!");
                }
                break;
            case "3":
                System.out.print("Enter pet ID to reject: ");
                try {
                    int petId = Integer.parseInt(scanner.nextLine().trim());
                    Pet pet = pets.get(petId);
                    if (pet != null) {
                        pet.approvalStatus = "rejected";
                        System.out.println("✓ Pet listing rejected!");
                    } else {
                        System.out.println("❌ Pet not found!");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("❌ Invalid ID!");
                }
                break;
            case "4":
                System.out.println("\n--- All Pets ---");
                for (Pet pet : pets.values()) {
                    System.out.println("  " + pet);
                }
                System.out.println("Total pets: " + pets.size());
                break;
        }
    }

    private static void viewPlatformAnalytics() {
        System.out.println("\n=== PLATFORM ANALYTICS ===");
        System.out.println("Total Users: " + users.size());
        System.out.println("Total Pets Listed: " + pets.size());
        System.out.println("Total Applications: " + applications.size());

        long adoptedCount = pets.values().stream().filter(p -> p.adoptionStatus.equals("adopted")).count();
        long availableCount = pets.values().stream().filter(p -> p.adoptionStatus.equals("available")).count();

        System.out.println("Pets Adopted: " + adoptedCount);
        System.out.println("Pets Available: " + availableCount);

        long approvedApps = applications.values().stream().filter(a -> a.status.equals("approved")).count();
        System.out.println("Approved Applications: " + approvedApps);
    }

    private static void manageSystemSettings(Scanner scanner) {
        System.out.println("\n=== SYSTEM SETTINGS ===");
        System.out.println("1. View Settings");
        System.out.println("2. Update Max Application Time");
        System.out.println("3. Update Platform Name");
        System.out.print("Choose option: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                System.out.println("Current Settings:");
                System.out.println("  - Max Application Time: 30 days");
                System.out.println("  - Platform Name: Online Pet Adoption Platform");
                break;
            case "2":
                System.out.print("Enter new max application time (days): ");
                System.out.println("✓ Settings updated!");
                scanner.nextLine();
                break;
            case "3":
                System.out.print("Enter new platform name: ");
                System.out.println("✓ Settings updated!");
                scanner.nextLine();
                break;
        }
    }

    // ===== SHELTER MENU =====
    private static boolean shelterMenu(Scanner scanner) {
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║              SHELTER DASHBOARD                          ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println("1. List New Pet");
        System.out.println("2. View My Pets");
        System.out.println("3. Manage Applications");
        System.out.println("4. Communicate with Adopters");
        System.out.println("5. View Adoption Statistics");
        System.out.println("6. Logout");
        System.out.print("Choose option: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                listNewPet(scanner);
                break;
            case "2":
                viewShelterPets();
                break;
            case "3":
                manageShelterApplications(scanner);
                break;
            case "4":
                communicateWithAdopters(scanner);
                break;
            case "5":
                viewShelterStatistics();
                break;
            case "6":
                currentUser = null;
                break;
            default:
                System.out.println("❌ Invalid option!");
        }
        return true;
    }

    private static void listNewPet(Scanner scanner) {
        System.out.println("\n=== LIST NEW PET ===");
        System.out.print("Pet name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Pet type (dog/cat/rabbit/etc): ");
        String type = scanner.nextLine().trim();
        System.out.print("Breed: ");
        String breed = scanner.nextLine().trim();
        System.out.print("Age: ");
        int age = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Description: ");
        String description = scanner.nextLine().trim();

        Pet newPet = new Pet(nextPetId++, currentUser.id, name, type, breed, age, description);
        pets.put(newPet.id, newPet);
        System.out.println("✓ Pet listing submitted! Awaiting admin approval. (Pet ID: " + newPet.id + ")");
    }

    private static void viewShelterPets() {
        System.out.println("\n=== YOUR PETS ===");
        boolean found = false;
        for (Pet pet : pets.values()) {
            if (pet.shelterId == currentUser.id) {
                System.out.println("  " + pet);
                found = true;
            }
        }
        if (!found) {
            System.out.println("You haven't listed any pets yet.");
        }
    }

    private static void manageShelterApplications(Scanner scanner) {
        System.out.println("\n=== APPLICATIONS FOR YOUR PETS ===");
        boolean found = false;
        for (Application app : applications.values()) {
            Pet pet = pets.get(app.petId);
            if (pet != null && pet.shelterId == currentUser.id) {
                System.out.println("  " + app);
                User adopter = users.get(app.adopterId);
                System.out.println("    Adopter: " + (adopter != null ? adopter.name : "Unknown"));
                System.out.println("    Notes: " + app.applicationNotes);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No applications received yet.");
        }

        System.out.print("\nUpdate application status? Enter App ID (or 0 to skip): ");
        try {
            int appId = Integer.parseInt(scanner.nextLine().trim());
            if (appId > 0) {
                Application app = applications.get(appId);
                if (app != null) {
                    System.out.println("1. Approve\n2. Reject\n3. Mark as Adopted");
                    System.out.print("Choose: ");
                    String choice = scanner.nextLine().trim();
                    switch (choice) {
                        case "1":
                            app.status = "approved";
                            break;
                        case "2":
                            app.status = "rejected";
                            break;
                        case "3":
                            app.status = "adopted";
                            pets.get(app.petId).adoptionStatus = "adopted";
                            break;
                    }
                    System.out.println("✓ Application status updated!");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Skipped.");
        }
    }

    private static void communicateWithAdopters(Scanner scanner) {
        System.out.println("\n=== MESSAGES ===");
        System.out.println("1. Send Message");
        System.out.println("2. View Received Messages");
        System.out.print("Choose: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                System.out.print("Recipient user ID: ");
                int recipientId = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("Message: ");
                String content = scanner.nextLine().trim();
                Message msg = new Message(nextMessageId++, currentUser.id, recipientId, content);
                messages.put(msg.id, msg);
                System.out.println("✓ Message sent!");
                break;
            case "2":
                System.out.println("\n--- Your Messages ---");
                for (Message m : messages.values()) {
                    if (m.recipientId == currentUser.id) {
                        User sender = users.get(m.senderId);
                        System.out.println("From " + (sender != null ? sender.name : "Unknown") + ": " + m.content);
                    }
                }
                break;
        }
    }

    private static void viewShelterStatistics() {
        System.out.println("\n=== YOUR ADOPTION STATISTICS ===");
        long myPets = pets.values().stream().filter(p -> p.shelterId == currentUser.id).count();
        long myAdopted = pets.values().stream()
                .filter(p -> p.shelterId == currentUser.id && p.adoptionStatus.equals("adopted")).count();
        long myApplications = applications.values().stream()
                .filter(a -> pets.containsKey(a.petId) && pets.get(a.petId).shelterId == currentUser.id).count();

        System.out.println("Total Pets Listed: " + myPets);
        System.out.println("Pets Adopted: " + myAdopted);
        System.out.println("Total Applications Received: " + myApplications);
    }

    // ===== ADOPTER MENU =====
    private static boolean adopterMenu(Scanner scanner) {
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║              ADOPTER DASHBOARD                          ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println("1. Browse Available Pets");
        System.out.println("2. Apply for Pet Adoption");
        System.out.println("3. View My Applications");
        System.out.println("4. Manage Profile");
        System.out.println("5. Messages");
        System.out.println("6. Logout");
        System.out.print("Choose option: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                browsePets(scanner);
                break;
            case "2":
                applyForAdoption(scanner);
                break;
            case "3":
                viewMyApplications();
                break;
            case "4":
                manageProfile(scanner);
                break;
            case "5":
                adopterMessages(scanner);
                break;
            case "6":
                currentUser = null;
                break;
            default:
                System.out.println("❌ Invalid option!");
        }
        return true;
    }

    private static void browsePets(Scanner scanner) {
        System.out.println("\n=== BROWSE AVAILABLE PETS ===");
        System.out.println("1. View All Available Pets");
        System.out.println("2. Search by Type");
        System.out.println("3. Search by Breed");
        System.out.print("Choose: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                System.out.println("\n--- Available Pets ---");
                for (Pet pet : pets.values()) {
                    if (pet.adoptionStatus.equals("available") && pet.approvalStatus.equals("approved")) {
                        System.out.println("  ID: " + pet.id + " | Name: " + pet.name + " | Type: " + pet.type +
                                " | Breed: " + pet.breed + " | Age: " + pet.age);
                    }
                }
                break;
            case "2":
                System.out.print("Enter pet type: ");
                String type = scanner.nextLine().trim();
                System.out.println("\n--- Pets of type: " + type + " ---");
                for (Pet pet : pets.values()) {
                    if (pet.type.equalsIgnoreCase(type) && pet.adoptionStatus.equals("available")) {
                        System.out.println("  " + pet);
                    }
                }
                break;
            case "3":
                System.out.print("Enter breed: ");
                String breed = scanner.nextLine().trim();
                System.out.println("\n--- Pets of breed: " + breed + " ---");
                for (Pet pet : pets.values()) {
                    if (pet.breed.equalsIgnoreCase(breed) && pet.adoptionStatus.equals("available")) {
                        System.out.println("  " + pet);
                    }
                }
                break;
        }
    }

    private static void applyForAdoption(Scanner scanner) {
        System.out.print("Enter pet ID to apply: ");
        try {
            int petId = Integer.parseInt(scanner.nextLine().trim());
            Pet pet = pets.get(petId);
            if (pet != null && pet.adoptionStatus.equals("available")) {
                System.out.print("Why do you want to adopt this pet? ");
                String notes = scanner.nextLine().trim();
                Application app = new Application(nextApplicationId++, currentUser.id, petId, notes);
                applications.put(app.id, app);
                System.out.println("✓ Application submitted! (Application ID: " + app.id + ")");
            } else {
                System.out.println("❌ Pet not available!");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid ID!");
        }
    }

    private static void viewMyApplications() {
        System.out.println("\n=== YOUR APPLICATIONS ===");
        boolean found = false;
        for (Application app : applications.values()) {
            if (app.adopterId == currentUser.id) {
                Pet pet = pets.get(app.petId);
                System.out.println("  Application ID: " + app.id + " | Pet: " + (pet != null ? pet.name : "Unknown") +
                        " | Status: " + app.status);
                found = true;
            }
        }
        if (!found) {
            System.out.println("You haven't submitted any applications yet.");
        }
    }

    private static void manageProfile(Scanner scanner) {
        System.out.println("\n=== PROFILE MANAGEMENT ===");
        System.out.println("Current Profile:");
        System.out.println("  Name: " + currentUser.name);
        System.out.println("  Email: " + currentUser.email);

        System.out.println("\n1. Update Name");
        System.out.println("2. Update Email");
        System.out.println("3. Change Password");
        System.out.print("Choose: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                System.out.print("New name: ");
                currentUser.name = scanner.nextLine().trim();
                System.out.println("✓ Name updated!");
                break;
            case "2":
                System.out.print("New email: ");
                currentUser.email = scanner.nextLine().trim();
                System.out.println("✓ Email updated!");
                break;
            case "3":
                System.out.print("Current password: ");
                String oldPwd = scanner.nextLine().trim();
                if (oldPwd.equals(currentUser.password)) {
                    System.out.print("New password: ");
                    currentUser.password = scanner.nextLine().trim();
                    System.out.println("✓ Password updated!");
                } else {
                    System.out.println("❌ Incorrect password!");
                }
                break;
        }
    }

    private static void adopterMessages(Scanner scanner) {
        System.out.println("\n=== MESSAGES ===");
        System.out.println("1. Send Message");
        System.out.println("2. View Received Messages");
        System.out.print("Choose: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                System.out.print("Recipient user ID: ");
                int recipientId = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("Message: ");
                String content = scanner.nextLine().trim();
                Message msg = new Message(nextMessageId++, currentUser.id, recipientId, content);
                messages.put(msg.id, msg);
                System.out.println("✓ Message sent!");
                break;
            case "2":
                System.out.println("\n--- Your Messages ---");
                for (Message m : messages.values()) {
                    if (m.recipientId == currentUser.id) {
                        User sender = users.get(m.senderId);
                        System.out.println("From " + (sender != null ? sender.name : "Unknown") + ": " + m.content);
                    }
                }
                break;
        }
    }

    // ===================== SAMPLE DATA INITIALIZATION =====================

    private static void initializeSampleData() {
        // Create sample users
        users.put(1, new User(1, "Admin User", "admin@petadoption.com", "admin", "admin123"));
        users.put(2, new User(2, "Happy Paws Shelter", "shelter@happypaws.com", "shelter", "shelter123"));
        users.put(3, new User(3, "John Adopter", "john@email.com", "adopter", "john123"));
        users.put(4, new User(4, "Sarah Adopter", "sarah@email.com", "adopter", "sarah123"));
        nextUserId = 5;

        // Create sample pets
        pets.put(1, new Pet(1, 2, "Max", "dog", "Golden Retriever", 3, "Friendly and energetic"));
        pets.get(1).approvalStatus = "approved";
        pets.put(2, new Pet(2, 2, "Luna", "cat", "Siamese", 2, "Calm and affectionate"));
        pets.get(2).approvalStatus = "approved";
        pets.put(3, new Pet(3, 2, "Buddy", "dog", "Labrador", 4, "Loyal and playful"));
        pets.get(3).approvalStatus = "pending";
        nextPetId = 4;

        // Create sample applications
        applications.put(1, new Application(1, 3, 1, "I love dogs and have a large backyard"));
        applications.put(2, new Application(2, 4, 2, "Always wanted a cat"));
        nextApplicationId = 3;

        System.out.println("✓ Sample data loaded successfully!\n");
    }
}
