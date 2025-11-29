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
 * ApplicationServlet - Handles adoption applications
 * GET /api/applications - List applications
 * POST /api/applications - Submit adoption application
 * PUT /api/applications - Update application status (Shelter/Admin only)
 */
public class ApplicationServlet extends HttpServlet {
    private static final Gson gson = new Gson();
    private static List<ApplicationData> applicationDatabase = new ArrayList<>();
    private static int applicationCounter = 1;

    static class ApplicationData {
        int id;
        int adopterId;
        int petId;
        String status;
        String applicationNotes;
        long createdAt;

        ApplicationData(int adopterId, int petId, String applicationNotes) {
            this.id = applicationCounter++;
            this.adopterId = adopterId;
            this.petId = petId;
            this.status = "submitted";
            this.applicationNotes = applicationNotes;
            this.createdAt = System.currentTimeMillis();
        }
    }

    // Initialize demo data
    static {
        applicationDatabase.add(new ApplicationData(3, 1, "I love dogs and have a big backyard!"));
        applicationDatabase.add(new ApplicationData(4, 2, "Cats are perfect for my apartment."));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.println("{\"success\": false, \"message\": \"Not authenticated\"}");
            return;
        }

        String userRole = (String) session.getAttribute("userRole");
        int userId = Integer.parseInt((String) session.getAttribute("userId"));

        JsonArray applicationsArray = new JsonArray();

        for (ApplicationData app : applicationDatabase) {
            // Adopters see only their applications
            if ("adopter".equals(userRole) && app.adopterId != userId) {
                continue;
            }
            // Shelters see applications for their pets (would need pet-shelter mapping)
            // For demo, we show all. In production, filter by shelterPets.

            applicationsArray.add(applicationToJson(app));
        }

        response.setStatus(HttpServletResponse.SC_OK);
        out.println(applicationsArray.toString());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || !"adopter".equals(session.getAttribute("userRole"))) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.println("{\"success\": false, \"message\": \"Only adopters can submit applications\"}");
            return;
        }

        try {
            StringBuilder sb = new StringBuilder();
            String line;
            java.io.BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JsonObject appRequest = gson.fromJson(sb.toString(), JsonObject.class);
            int adopterId = Integer.parseInt((String) session.getAttribute("userId"));
            int petId = appRequest.get("petId").getAsInt();
            String notes = appRequest.get("notes").getAsString();

            // Check for duplicate applications
            for (ApplicationData app : applicationDatabase) {
                if (app.adopterId == adopterId && app.petId == petId
                        && !("rejected".equals(app.status) || "adopted".equals(app.status))) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("{\"success\": false, \"message\": \"You already have an active application for this pet\"}");
                    return;
                }
            }

            ApplicationData newApp = new ApplicationData(adopterId, petId, notes);
            applicationDatabase.add(newApp);

            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("success", true);
            responseJson.addProperty("message", "Application submitted successfully");
            responseJson.addProperty("applicationId", newApp.id);

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
        String userRole = session != null ? (String) session.getAttribute("userRole") : null;

        if (session == null || (!("shelter".equals(userRole) || "admin".equals(userRole)))) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.println("{\"success\": false, \"message\": \"Shelter or Admin access required\"}");
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
            int applicationId = updateRequest.get("applicationId").getAsInt();
            String newStatus = updateRequest.get("status").getAsString();

            for (ApplicationData app : applicationDatabase) {
                if (app.id == applicationId) {
                    app.status = newStatus;

                    JsonObject responseJson = new JsonObject();
                    responseJson.addProperty("success", true);
                    responseJson.addProperty("message", "Application status updated to: " + newStatus);

                    response.setStatus(HttpServletResponse.SC_OK);
                    out.println(responseJson.toString());
                    return;
                }
            }

            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.println("{\"success\": false, \"message\": \"Application not found\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"success\": false, \"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    private JsonObject applicationToJson(ApplicationData app) {
        JsonObject json = new JsonObject();
        json.addProperty("id", app.id);
        json.addProperty("adopterId", app.adopterId);
        json.addProperty("petId", app.petId);
        json.addProperty("status", app.status);
        json.addProperty("notes", app.applicationNotes);
        json.addProperty("submittedAt", app.createdAt);
        return json;
    }
}
