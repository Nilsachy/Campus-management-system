package nl.tudelft.oopp.cars.communication;

import static nl.tudelft.oopp.cars.communication.RequestHandler.handleRequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

import nl.tudelft.oopp.cars.logic.GeneralLogic;
import nl.tudelft.oopp.shared.requests.create.ChangePasswordRequest;
import nl.tudelft.oopp.shared.requests.create.CreateUserRequest;
import nl.tudelft.oopp.shared.requests.read.GetUserValidationRequest;

public class ServerUserCommunication {

    private static HttpClient client = HttpClient.newBuilder().build();

    /**
     * POST: Sends an email and password combination to the server.
     *
     * @param email email address of user
     * @param passwordHash hash of the password
     * @return whether or not the email and password combination is valid and existent
     */
    public static String validateLogin(String email, int passwordHash) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(new GetUserValidationRequest(email, passwordHash));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/validateLogin")).build();
        String response = handleRequest(request);

        if (response == null) {
            return "No Response";
        }

        System.out.println(response);
        if (response.startsWith("ERROR user")) {
            response = "ERROR missing";
        } else if (response.startsWith("ERROR wrong")) {
            response = "ERROR password";
        } else {
            response = "SUCCESS";
        }
        return response;
    }

    /**
     * POST: Sends an email, password (hashed) and role combination to the server.
     *
     * @param email email of the new user
     * @param passwordHash hash of the password
     * @return a confirmation of the user account being accepted and created (IFF it is valid)
     */
    public static String addUser(String email, int passwordHash) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(new CreateUserRequest(email, passwordHash));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/addUser")).build();
        return handleRequest(request);
    }

    /**
     * POST: Sends an email and password (hashed) combination to the server.
     *
     * @param email email of the user
     * @param password new password of the user
     * @return a confirmation of the password being changed
     */
    public static String changePassword(String email, int password) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(new ChangePasswordRequest(email, password));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/changePassword")).build();
        return handleRequest(request);
    }

    /**
     * GET: Retrieves the currently logged in user from the server.
     * @return username
     */
    public static String getThisUser() throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET().uri(URI.create("http://localhost:8080/getThisUser")).build();
        return handleRequest(request);
    }

    /**
     * GET: Retrieves the currently logged in user's role from the server.
     * @return role
     */
    public static String getThisRole() throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET().uri(URI.create("http://localhost:8080/getThisRole")).build();
        return handleRequest(request);
    }
}
