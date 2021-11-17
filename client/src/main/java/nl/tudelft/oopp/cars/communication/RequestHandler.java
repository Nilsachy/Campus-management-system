package nl.tudelft.oopp.cars.communication;

import java.io.IOException;
import java.net.CookieManager;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import nl.tudelft.oopp.cars.logic.GeneralLogic;

public class RequestHandler {

    private static HttpClient client = HttpClient.newBuilder()
                                        .cookieHandler(new CookieManager()).build();

    /**
     * Safely handles a request and deals with the chance of it failing.
     *
     * @return The JSON body of a request to the server
     */
    public static String handleRequest(HttpRequest request) throws IOException {
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (response.statusCode() != 200) {
            System.out.println("Status: " + response.statusCode());
        }
        GeneralLogic.sessionDenial(response.body());
        return response.body();
    }
}
