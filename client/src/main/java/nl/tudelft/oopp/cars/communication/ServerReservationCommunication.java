package nl.tudelft.oopp.cars.communication;

import static nl.tudelft.oopp.cars.communication.RequestHandler.handleRequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import nl.tudelft.oopp.shared.requests.create.CreateBikeRentalRequest;
import nl.tudelft.oopp.shared.requests.create.CreateRoomReservationRequest;

import nl.tudelft.oopp.shared.requests.delete.DeleteBikeReservationRequest;
import nl.tudelft.oopp.shared.requests.delete.DeleteRoomReservationRequest;
import nl.tudelft.oopp.shared.requests.delete.RemoveBuildingRequest;
import nl.tudelft.oopp.shared.requests.delete.RemoveEventByIdRequest;
import nl.tudelft.oopp.shared.requests.read.GetAvailableBikeStoragesRequest;
import nl.tudelft.oopp.shared.requests.read.GetAvailableRoomsRequest;
import nl.tudelft.oopp.shared.requests.read.GetEventsInSpanRequest;
import nl.tudelft.oopp.shared.requests.read.GetReservationsRequest;
import nl.tudelft.oopp.shared.responses.content.AvailableBikeStoragesResponse;
import nl.tudelft.oopp.shared.responses.content.BikeReservationResponse;
import nl.tudelft.oopp.shared.responses.content.CustomEventsResponse;
import nl.tudelft.oopp.shared.responses.content.ReservationResponse;
import nl.tudelft.oopp.shared.responses.content.ReservationsResponse;
import nl.tudelft.oopp.shared.responses.content.RoomReservationResponse;
import nl.tudelft.oopp.shared.responses.content.RoomsResponse;

public class ServerReservationCommunication {

    /**
    * Post: Retrieves a list of all current reservations from the server.
    *
    * @param email email Address of the user
    * @return Json string containing all current reservations on the server database
    */
    public static List<ReservationResponse> getReservations(String email, String previousCurrent)
            throws IOException {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(new GetReservationsRequest(email, previousCurrent));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/getReservations")).build();
        String responseMessage = RequestHandler.handleRequest(request);

        try {
            ReservationsResponse response = gson.fromJson(responseMessage,
                    ReservationsResponse.class);
            List<RoomReservationResponse> r = response.getRoomReservationResponses();
            List<BikeReservationResponse> b = response.getBikeReservationResponses();
            List<ReservationResponse> reservationResponses = new ArrayList<ReservationResponse>();
            reservationResponses.addAll(r);
            reservationResponses.addAll(b);
            return reservationResponses;
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }

    /**
     * Post: Retrieves a list of all current reservations from the server.
     *
     * @param email email Address of the user
     * @return Json string containing all current reservations on the server database
     */
    public static CustomEventsResponse getCustomEvents(String email, String previousCurrent)
            throws IOException {
        Gson gson = new GsonBuilder().create();
        String json;

        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, 2);
        Calendar d = Calendar.getInstance();
        d.add(Calendar.YEAR, -1);
        if (previousCurrent.equals("active")) {
            json = gson.toJson(new GetEventsInSpanRequest(email, Calendar.getInstance(), c));
        } else {
            json = gson.toJson(new GetEventsInSpanRequest(email, d, Calendar.getInstance()));
        }

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/getCustomEventsInTimespanByUser")).build();
        String responseMessage = RequestHandler.handleRequest(request);

        try {
            CustomEventsResponse response = gson.fromJson(responseMessage,
                    CustomEventsResponse.class);
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
     * POST: Reserves a room with the given information.
     *
     * @param date date of the reservation
     * @param timeslot timeslot of the reservation
     * @param user the user that is reserving the room
     * @param faculty faculty the room is in
     * @param building building the room is in
     * @param room room name
     * @return a confirmation of the room being reserved (after validating)
     */
    public static String reserveRoom(Calendar date, String timeslot, String user,
                                     String faculty, String building, String room)
            throws IOException {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(new CreateRoomReservationRequest(date, timeslot, user,
                                                                    faculty, building, room));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/reserveRoom")).build();
        return handleRequest(request);
    }

    /**
     * POST: Retrieves a list of all available rooms from the server.
     *
     * @param building building that the rooms are in
     * @param date date that all the rooms should be available
     * @param timeslot timeslot that all the rooms should be available
     * @return Json string containing all available rooms on the server database
     */
    public static RoomsResponse getAvailableRooms(int building, Calendar date,
                                                  String timeslot) throws IOException {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(new GetAvailableRoomsRequest(date, timeslot, building));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/getAvailableRooms")).build();
        String responseMessage = RequestHandler.handleRequest(request);

        try {
            RoomsResponse response = gson.fromJson(responseMessage,
                    RoomsResponse.class);
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
     * POST: Rents a bike with the given information.
     *
     * @param user the user that is renting the bike
     * @param fromFaculty faculty to pick the bike up at
     * @param toFaculty faculty to drop the bike off at
     * @param pickupDate day to pick the bike up at
     * @param dropoffDate day to drop the bike off at
     * @return a confirmation of the bike being reserved (after validating)
     */
    public static String rentBike(String user, String fromFaculty, String toFaculty,
                                  Calendar pickupDate, Calendar dropoffDate) throws IOException {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(new CreateBikeRentalRequest(user, fromFaculty, toFaculty,
                pickupDate, dropoffDate));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/rentBike")).build();
        return handleRequest(request);
    }

    /**
     * POST: Retrieves a list of all available bike storages from the server.
     *
     * @param fromDate date and time from which the bike should be available
     * @param toDate the bike should be available until this date and time
     * @return Json string containing all available bike storages on the server database
     */
    public static AvailableBikeStoragesResponse getAvailableBikeStorages(Calendar fromDate,
                                                                         Calendar toDate)
            throws IOException {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(new GetAvailableBikeStoragesRequest(fromDate, toDate));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/getAvailableBikeStorages")).build();
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
     * POST: Removes a room reservation from the server.
     *
     * @param req the request object to be sent to the server
     * @return Json string containing a status update on the removal
     * @throws IOException called when there is a miscommunication
     */
    public static String removeRoomReservation(DeleteRoomReservationRequest req)
            throws IOException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(req);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/cancelRoom")).build();
        return RequestHandler.handleRequest(request);
    }

    /**
     * POST: Removes a bike reservation from the server.
     *
     * @param req the request object to be sent to the server
     * @return Json string containing a status update on the removal
     * @throws IOException called when there is a miscommunication
     */
    public static String removeBikeReservation(DeleteBikeReservationRequest req)
            throws IOException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(req);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/deleteBikeReservation")).build();
        return RequestHandler.handleRequest(request);
    }

    /**
     * POST: Removes a custom event from the server.
     *
     * @param req the request object to be sent to the server
     * @return Json string containing a status update on the removal
     * @throws IOException called when there is a miscommunication
     */
    public static String removeCustomEvent(RemoveEventByIdRequest req) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(req);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/removeCustomEventsById")).build();
        return RequestHandler.handleRequest(request);
    }
}
