package nl.tudelft.oopp.cars.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.tudelft.oopp.cars.constants.StringResponses;
import nl.tudelft.oopp.cars.entities.RoomReservation;
import nl.tudelft.oopp.cars.repositories.RoomReservationRepository;
import nl.tudelft.oopp.shared.requests.create.CreateRoomReservationRequest;
import nl.tudelft.oopp.shared.requests.delete.DeleteRoomReservationRequest;
import nl.tudelft.oopp.shared.requests.read.GetByEmailRequest;
import nl.tudelft.oopp.shared.responses.content.ReservationsResponse;
import nl.tudelft.oopp.shared.responses.content.RoomReservationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RoomReservationController {

    Logger logger = LoggerFactory.getLogger(RoomReservationController.class);

    @Autowired
    private RoomReservationRepository repository;


    /**
     * Gets all current room reservations.
     *
     * @return List of all RoomReservations in the database
     */
    @PostMapping("getAllRoomReservations")
    public @ResponseBody String getAllRoomReservations(HttpServletRequest req,
                                                       HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        logger.debug("Getting all reservations");

        List<RoomReservation> reservations = repository.getAllReservation();
        Gson gson = new GsonBuilder().create();

        List<RoomReservationResponse> roomReservationResponses
                = new ArrayList<RoomReservationResponse>();

        for (RoomReservation r:reservations) {
            roomReservationResponses.add(new RoomReservationResponse(r.getId(),
                    r.getDate(), r.getTimeslot(), r.getUser(),
                    r.getFaculty(), r.getBuilding(), r.getRoom()));
        }

        ReservationsResponse reservationsResponse = new
                ReservationsResponse(null, roomReservationResponses);
        return gson.toJson(reservationsResponse);
    }

    /**
     * Reserves a room.
     *
     * @param json the json object string of the requested room
     * @return empty string
     */
    @PostMapping("reserveRoom")
    public @ResponseBody String reserveRoom(@RequestBody String json, HttpServletRequest req,
                                            HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        CreateRoomReservationRequest request =
                gson.fromJson(json, CreateRoomReservationRequest.class);

        logger.debug("Adding a reservation for {}", request.toString());

        RoomReservation roomReservation = new RoomReservation(request.getDate(),
                request.getTimeSlot(), request.getUser(), request.getFaculty(),
                request.getBuilding(), request.getRoom());

        repository.save(roomReservation);

        return "Room Reservation Saved.";
    }

    /**
     * Delete a room.
     * This method is for junit at the moment
     * @param json the json object string of the requested room
     * @return empty string
     */
    @PostMapping("cancelRoom")
    public @ResponseBody String cancelRoom(@RequestBody String json, HttpServletRequest req,
                                           HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        DeleteRoomReservationRequest request =
                gson.fromJson(json, DeleteRoomReservationRequest.class);


        repository.deleteById(request.getId());
        return "Room Reservation Deleted.";

    }

    /**
     * Find all the room reservations for a given user.
     * @param json The JSON request that contains the users email.
     * @return The list of bike reservations for a user in JSON format
     */
    @PostMapping(path = "findRoomReservationByUser")
    public @ResponseBody String findRoomReservationByUser(@RequestBody String json,
                                                          HttpServletRequest req,
                                                          HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();
        GetByEmailRequest request = gson.fromJson(json, GetByEmailRequest.class);

        logger.debug("Finding reservations for user {}", request.getEmail());

        List<RoomReservation> result = repository.getByUser(request.getEmail());

        logger.debug("Found {} bike reservations for user {}", result.size(), request.getEmail());

        List<RoomReservationResponse> roomReservationResponses =
                new ArrayList<RoomReservationResponse>();

        for (RoomReservation r:result) {
            roomReservationResponses.add(new RoomReservationResponse(r.getId(),
                    r.getDate(), r.getTimeslot(), r.getUser(),
                    r.getFaculty(), r.getBuilding(), r.getRoom()));
        }

        ReservationsResponse reservationsResponse = new
                ReservationsResponse(null, roomReservationResponses);
        return gson.toJson(reservationsResponse);
    }
}
