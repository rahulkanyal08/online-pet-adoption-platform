import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * LoginServlet - Handles user authentication
 * POST /api/login - Authenticate user with email and password
 * GET /api/logout - Logout user
 */
public class LoginServlet extends HttpServlet {
    private static final Gson gson = new Gson();

    // Demo users (in production, query database)
    static class User {
        String id, name, email, role, password;
        User(String id, String name, String email, String role, String password) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.role = role;
            this.password = password;
        }
    }

    private static final User[] DEMO_USERS = {
        new User("1", "Admin User", "admin@petadoption.com", "admin", "admin123"),
        new User("2", "Happy Paws Shelter", "shelter@happypaws.com", "shelter", "shelter123"),
        new User("3", "John Adopter", "john@email.com", "adopter", "john123"),
        new User("4", "Sarah Adopter", "sarah@email.com", "adopter", "sarah123")
    };

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Parse request body
            StringBuilder sb = new StringBuilder();
            String line;
            java.io.BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JsonObject loginRequest = gson.fromJson(sb.toString(), JsonObject.class);
            String email = loginRequest.get("email").getAsString();
            String password = loginRequest.get("password").getAsString();

            // Validate credentials
            User authenticatedUser = null;
            for (User user : DEMO_USERS) {
                if (user.email.equals(email) && user.password.equals(password)) {
                    authenticatedUser = user;
                    break;
                }
            }

            if (authenticatedUser != null) {
                // Create session
                HttpSession session = request.getSession(true);
                session.setAttribute("userId", authenticatedUser.id);
                session.setAttribute("userEmail", authenticatedUser.email);
                session.setAttribute("userName", authenticatedUser.name);
                session.setAttribute("userRole", authenticatedUser.role);
                session.setMaxInactiveInterval(30 * 60); // 30 minutes

                // Return success response
                JsonObject responseJson = new JsonObject();
                responseJson.addProperty("success", true);
                responseJson.addProperty("message", "Login successful");
                responseJson.addProperty("userId", authenticatedUser.id);
                responseJson.addProperty("userName", authenticatedUser.name);
                responseJson.addProperty("userRole", authenticatedUser.role);

                response.setStatus(HttpServletResponse.SC_OK);
                out.println(responseJson.toString());
            } else {
                // Invalid credentials
                JsonObject responseJson = new JsonObject();
                responseJson.addProperty("success", false);
                responseJson.addProperty("message", "Invalid email or password");

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.println(responseJson.toString());
            }
        } catch (Exception e) {
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("success", false);
            errorJson.addProperty("message", "Error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(errorJson.toString());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("logout".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.setContentType("application/json");
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("success", true);
            responseJson.addProperty("message", "Logout successful");
            response.getWriter().println(responseJson.toString());
        } else if ("status".equals(action)) {
            response.setContentType("application/json");
            HttpSession session = request.getSession(false);
            JsonObject responseJson = new JsonObject();

            if (session != null && session.getAttribute("userId") != null) {
                responseJson.addProperty("loggedIn", true);
                responseJson.addProperty("userName", (String) session.getAttribute("userName"));
                responseJson.addProperty("userRole", (String) session.getAttribute("userRole"));
            } else {
                responseJson.addProperty("loggedIn", false);
            }
            response.getWriter().println(responseJson.toString());
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{\"error\": \"Unknown action\"}");
        }
    }
}
