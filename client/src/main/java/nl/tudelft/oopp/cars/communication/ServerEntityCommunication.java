package nl.tudelft.oopp.cars.communication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Calendar;

import nl.tudelft.oopp.shared.requests.create.AddBikeStorageRequest;
import nl.tudelft.oopp.shared.requests.create.AddBuildingRequest;
import nl.tudelft.oopp.shared.requests.create.CreateAnnouncementRequest;
import nl.tudelft.oopp.shared.requests.create.CreateCustomEventRequest;
import nl.tudelft.oopp.shared.requests.create.CreateRoomRequest;
import nl.tudelft.oopp.shared.requests.delete.RemoveAnnouncementRequest;
import nl.tudelft.oopp.shared.requests.delete.RemoveBikeStorageRequest;
import nl.tudelft.oopp.shared.requests.delete.RemoveBuildingRequest;
import nl.tudelft.oopp.shared.requests.delete.RemoveEventByIdRequest;
import nl.tudelft.oopp.shared.requests.delete.RemoveRoomByIdRequest;
import nl.tudelft.oopp.shared.requests.read.GetByBuildingIdRequest;

import nl.tudelft.oopp.shared.responses.content.AnnouncementsResponse;
import nl.tudelft.oopp.shared.responses.content.AvailableBikeStoragesResponse;
import nl.tudelft.oopp.shared.responses.content.BuildingResponse;
import nl.tudelft.oopp.shared.responses.content.BuildingsResponse;
import nl.tudelft.oopp.shared.responses.content.FacultiesResponse;
import nl.tudelft.oopp.shared.responses.content.RoomsResponse;

public class ServerEntityCommunication {

    /**
     * GET: Retrieves a list of all buildings from the server.
     *
     * @return Json string containing all buildings on the server database
     */
    public static BuildingsResponse getAllBuildings() throws IOException {
        Gson gson = new GsonBuilder().create();

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/getAllBuildings")).build();

        String responseMessage = RequestHandler.handleRequest(request);

        try {
            BuildingsResponse response = gson.fromJson(responseMessage,
                    BuildingsResponse.class);
            if (response != null) {
                return response;
            }
            System.out.println(responseMessage);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        return null;
    }

    /**
     * POST: Retrieves the building with the given id from the server.
     *
     * @return Json string containing the building with the given id
     */
    public static String getBuildingById(int id) throws IOException {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(new GetByBuildingIdRequest(id));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/getBuildingById")).build();
        String responseMessage = RequestHandler.handleRequest(request);

        try {
            BuildingResponse response = gson.fromJson(responseMessage, BuildingResponse.class);
            if (response != null) {
                return responseMessage;
            }
            System.out.println(responseMessage);
        } catch (Exception ex) {
            System.out.println("ITS PARSING AGAIN " + ex.toString());
        }
        return null;
    }

    /**
     * POST: Sends a new building to the server.
     *
     * @return the body of a post request to the server
     */
    public static String addBuilding(int id, String name, String address, String faculty)
            throws IOException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        //TODO: check whether new structure of DB with id works
        String json = gson.toJson(new AddBuildingRequest(id, name, address, faculty));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/addBuilding")).build();
        return RequestHandler.handleRequest(request);
    }

    /**
     * POST: Removes a building from the server.
     *
     * @return the body of a post request to the server
     */
    public static String removeBuilding(int name) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(new RemoveBuildingRequest(name));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/removeBuilding")).build();
        return RequestHandler.handleRequest(request);
    }

    /**
     * GET: Retrieves a list of all rooms from the server.
     *
     * @return Json string containing all rooms on the server database
     */
    public static RoomsResponse getAllRooms() throws IOException {
        Gson gson = new GsonBuilder().create();

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/getAllRooms")).build();
        String response = RequestHandler.handleRequest(request);

        try {
            RoomsResponse response1 = gson.fromJson(response,
                    RoomsResponse.class);
            if (response != null) {
                return response1;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        return null;
    }

    /**
     * POST: Sends a new room to the server.
     *
     * @return the body of a post request to the server
     */
    public static String addRoom(String faculty, int building, String room, int capacity,
                                                boolean hasWhiteboard, boolean staffOnly)
            throws IOException {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(new CreateRoomRequest(faculty, building, room, capacity,
                hasWhiteboard, staffOnly));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/addNewRoom")).build();
        return RequestHandler.handleRequest(request);
    }

    /**
     * POST: Removes a room from the server.
     *
     * @return the body of a post request to the server
     */
    public static String removeRoom(int roomId) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(new RemoveRoomByIdRequest(roomId));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/removeRoom")).build();
        return RequestHandler.handleRequest(request);
    }

    /**
     * GET: Retrieves a list of all bike storages from the server.
     *
     * @return Json string containing all bike storages on the server database
     */
    public static AvailableBikeStoragesResponse getAllBikeStorages() throws IOException {
        Gson gson = new GsonBuilder().create();

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/getAllBikeStorages")).build();
        String responseMessage = RequestHandler.handleRequest(request);

        try {
            AvailableBikeStoragesResponse response = gson.fromJson(responseMessage,
                    AvailableBikeStoragesResponse.class);
            if (response != null) {
                return response;
            }
            System.out.println(responseMessage);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        return null;
    }

    /**
     * GET: Retrieves a list of all bike storages from the server.
     *
     * @return Json string containing all bike storages on the server database
     */
    public static FacultiesResponse getAllFaculties() throws IOException {
        Gson gson = new GsonBuilder().create();

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/getAllFaculties")).build();
        String responseMessage = RequestHandler.handleRequest(request);

        try {
            FacultiesResponse response = gson.fromJson(responseMessage,
                    FacultiesResponse.class);
            if (response != null) {
                return response;
            }
            System.out.println(responseMessage);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        return null;
    }

    /**
     * POST: Sends a new bike storage to the server.
     *
     * @return the body of a post request to the server
     */
    public static String addBikeStorage(String faculty, int maxAvailable) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(new AddBikeStorageRequest(faculty, maxAvailable));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/addBikeStorage")).build();
        return RequestHandler.handleRequest(request);
    }

    /**
     * POST: Removes a bike storage from the server.
     *
     * @return the body of a post request to the server
     */
    public static String removeBikeStorage(String faculty) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(new RemoveBikeStorageRequest(faculty));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/removeBikeStorage")).build();
        return RequestHandler.handleRequest(request);
    }

    /**
     * GET: Retrieves a list of all announcements from the server.
     *
     * @return Json string containing all announcements on the server database
     */
    public static AnnouncementsResponse getAllAnnouncements() throws IOException {
        Gson gson = new GsonBuilder().create();

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/getAllAnnouncements")).build();
        String responseMessage = RequestHandler.handleRequest(request);

        try {
            AnnouncementsResponse response = gson.fromJson(responseMessage,
                    AnnouncementsResponse.class);
            if (response != null) {
                return response;
            }
            System.out.println(responseMessage);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        return null;
    }

    /**
     * POST: Sends a new announcement to the server.
     *
     * @return the body of a post request to the server
     */
    public static String addAnnouncement(String title, String content,
                                         Calendar calendar, String user) throws IOException {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(new CreateAnnouncementRequest(Calendar.getInstance(),
                calendar, title, content, user));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/addAnnouncement")).build();
        return RequestHandler.handleRequest(request);
    }

    /**
     * POST: Removes an announcement from the server.
     *
     * @return the body of a post request to the server
     */
    public static String removeAnnouncement(long id) throws IOException {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(new RemoveAnnouncementRequest(id));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/deleteAnnouncement")).build();
        return RequestHandler.handleRequest(request);
    }

    /**
     * POST: Sends a new custom event to the server.
     *
     * @return the body of a post request to the server
     */
    public static String addCustomEvent(String title, String content,
                                         String address, Calendar start,
                                         Calendar end, String user) throws IOException {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(new CreateCustomEventRequest(user, title, start,
                end, address, content));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/addCustomEvent")).build();
        return RequestHandler.handleRequest(request);
    }

    /**
     * POST: Removes a custom event from the server.
     *
     * @return the body of a post request to the server
     */
    public static String removeCustomEvent(long id) throws IOException {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(new RemoveEventByIdRequest(id));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/removeCustomEventsById")).build();
        return RequestHandler.handleRequest(request);
    }
}
