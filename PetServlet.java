import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * PetServlet - Handles pet operations
 * GET /api/pets - List all pets
 * GET /api/pets?filter=available - Filter pets
 * POST /api/pets - Add new pet (Shelter only)
 * PUT /api/pets - Update pet (Admin/Shelter only)
 * DELETE /api/pets/{id} - Delete pet (Admin only)
 */
public class PetServlet extends HttpServlet {
    private static final Gson gson = new Gson();
    private static List<PetData> petDatabase = new ArrayList<>();

    static class PetData {
        int id;
        int shelterId;
        String name, type, breed;
        int age;
        String description;
        String adoptionStatus, approvalStatus;

        PetData(int id, int shelterId, String name, String type, String breed, int age,
                String description, String adoptionStatus, String approvalStatus) {
            this.id = id;
            this.shelterId = shelterId;
            this.name = name;
            this.type = type;
            this.breed = breed;
            this.age = age;
            this.description = description;
            this.adoptionStatus = adoptionStatus;
            this.approvalStatus = approvalStatus;
        }
    }

    // Initialize demo data
    static {
        petDatabase.add(new PetData(1, 2, "Buddy", "Dog", "Golden Retriever", 3,
                "Friendly and loyal", "available", "approved"));
        petDatabase.add(new PetData(2, 2, "Whiskers", "Cat", "Persian", 2,
                "Calm and affectionate", "available", "approved"));
        petDatabase.add(new PetData(3, 2, "Hoppy", "Rabbit", "Holland Lop", 1,
                "Energetic bunny", "pending", "pending"));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();
        String filter = request.getParameter("filter");

        JsonArray petsArray = new JsonArray();

        // Filter by status if requested
        for (PetData pet : petDatabase) {
            if (filter != null) {
                if ("available".equals(filter) && "available".equals(pet.adoptionStatus)) {
                    petsArray.add(petToJson(pet));
                } else if ("adopted".equals(filter) && "adopted".equals(pet.adoptionStatus)) {
                    petsArray.add(petToJson(pet));
                } else if ("approved".equals(filter) && "approved".equals(pet.approvalStatus)) {
                    petsArray.add(petToJson(pet));
                }
            } else {
                petsArray.add(petToJson(pet));
            }
        }

        response.setStatus(HttpServletResponse.SC_OK);
        out.println(petsArray.toString());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userRole") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.println("{\"success\": false, \"message\": \"Not authenticated\"}");
            return;
        }

        String userRole = (String) session.getAttribute("userRole");
        if (!"shelter".equals(userRole) && !"admin".equals(userRole)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.println("{\"success\": false, \"message\": \"Only shelters can add pets\"}");
            return;
        }

        try {
            StringBuilder sb = new StringBuilder();
            String line;
            java.io.BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JsonObject petRequest = gson.fromJson(sb.toString(), JsonObject.class);
            int shelterId = Integer.parseInt((String) session.getAttribute("userId"));

            // Generate ID
            int newId = petDatabase.isEmpty() ? 1 : petDatabase.get(petDatabase.size() - 1).id + 1;

            PetData newPet = new PetData(
                newId,
                shelterId,
                petRequest.get("name").getAsString(),
                petRequest.get("type").getAsString(),
                petRequest.get("breed").getAsString(),
                petRequest.get("age").getAsInt(),
                petRequest.get("description").getAsString(),
                "pending",
                "pending"
            );

            petDatabase.add(newPet);

            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("success", true);
            responseJson.addProperty("message", "Pet added successfully and pending approval");
            responseJson.addProperty("petId", newId);

            response.setStatus(HttpServletResponse.SC_CREATED);
            out.println(responseJson.toString());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"success\": false, \"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("userRole"))) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.println("{\"success\": false, \"message\": \"Admin access required\"}");
            return;
        }

        try {
            StringBuilder sb = new StringBuilder();
            String line;
            java.io.BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JsonObject updateRequest = gson.fromJson(sb.toString(), JsonObject.class);
            int petId = updateRequest.get("petId").getAsInt();
            String action = updateRequest.get("action").getAsString();

            for (PetData pet : petDatabase) {
                if (pet.id == petId) {
                    if ("approve".equals(action)) {
                        pet.approvalStatus = "approved";
                    } else if ("reject".equals(action)) {
                        pet.approvalStatus = "rejected";
                    } else if ("adopt".equals(action)) {
                        pet.adoptionStatus = "adopted";
                    }

                    JsonObject responseJson = new JsonObject();
                    responseJson.addProperty("success", true);
                    responseJson.addProperty("message", "Pet updated: " + action);
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.println(responseJson.toString());
                    return;
                }
            }

            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.println("{\"success\": false, \"message\": \"Pet not found\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"success\": false, \"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("userRole"))) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.println("{\"success\": false, \"message\": \"Admin access required\"}");
            return;
        }

        try {
            String pathInfo = request.getPathInfo();
            int petId = Integer.parseInt(pathInfo.substring(1));

            for (PetData pet : petDatabase) {
                if (pet.id == petId) {
                    petDatabase.remove(pet);
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.println("{\"success\": true, \"message\": \"Pet deleted\"}");
                    return;
                }
            }

            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.println("{\"success\": false, \"message\": \"Pet not found\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"success\": false, \"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    private JsonObject petToJson(PetData pet) {
        JsonObject json = new JsonObject();
        json.addProperty("id", pet.id);
        json.addProperty("shelterId", pet.shelterId);
        json.addProperty("name", pet.name);
        json.addProperty("type", pet.type);
        json.addProperty("breed", pet.breed);
        json.addProperty("age", pet.age);
        json.addProperty("description", pet.description);
        json.addProperty("adoptionStatus", pet.adoptionStatus);
        json.addProperty("approvalStatus", pet.approvalStatus);
        return json;
    }
}
