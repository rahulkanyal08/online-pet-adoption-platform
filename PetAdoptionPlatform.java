
// Imports
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PetAdoptionPlatform {

    // Simple Pet class for in-memory storage
    static class Pet {
        int id;
        String name;
        String breed;
        int age;
        boolean isAvailable;

        Pet(int id, String name, String breed, int age, boolean isAvailable) {
            this.id = id;
            this.name = name;
            this.breed = breed;
            this.age = age;
            this.isAvailable = isAvailable;
        }

        @Override
        public String toString() {
            return "ID: " + id + ", Name: " + name + ", Breed: " + breed +
                    ", Age: " + age + ", Available: " + isAvailable;
        }
    }

    // In-memory storage for pets
    private static List<Pet> pets = new ArrayList<>();
    private static int nextId = 1;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Pet Adoption Platform!");
        System.out.println("(Running in demo mode - data not persisted)");
        System.out.println();

        boolean running = true;
        while (running) {
            System.out.println("Menu:");
            System.out.println("1. Add Pet");
            System.out.println("2. View All Pets");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    addPet(scanner);
                    break;
                case 2:
                    viewAllPets();
                    break;
                case 3:
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
            System.out.println();
        }

        scanner.close();
    }

    // Method to add a new pet
    private static void addPet(Scanner scanner) {
        try {
            System.out.print("Enter pet name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter pet breed: ");
            String breed = scanner.nextLine().trim();

            System.out.print("Enter pet age: ");
            int age = scanner.nextInt();

            System.out.print("Is the pet available for adoption? (true/false): ");
            boolean isAvailable = scanner.nextBoolean();
            scanner.nextLine(); // consume newline

            Pet newPet = new Pet(nextId++, name, breed, age, isAvailable);
            pets.add(newPet);
            System.out.println("✓ Pet added successfully!");
        } catch (Exception e) {
            System.out.println("Error adding pet: " + e.getMessage());
        }
    }

    // Method to view all pets
    private static void viewAllPets() {
        if (pets.isEmpty()) {
            System.out.println("No pets available.");
        } else {
            System.out.println("=== Available Pets ===");
            for (Pet pet : pets) {
                System.out.println(pet);
            }
        }
    }
}

// // Output
// Welcome to the Pet Adoption Platform!
// 1. Add Pet
// 2. View All Pets
// Choose an option: 1
// Pets table is ready.
// Enter pet name: Buddy
// Enter pet breed: Golden Retriever
// Enter pet age: 3
// Is the pet available for adoption? (true/false): true
// Pet added successfully.

// Choose an option: 2
// ID: 1, Name: Buddy, Breed: Golden Retriever, Age: 3, Available: true
