package nl.tudelft.oopp.cars.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nl.tudelft.oopp.cars.constants.StringResponses;
import nl.tudelft.oopp.cars.entities.BikeReservation;
import nl.tudelft.oopp.cars.entities.RoomReservation;
import nl.tudelft.oopp.cars.entities.User;
import nl.tudelft.oopp.cars.repositories.BikeReservationRepository;
import nl.tudelft.oopp.cars.repositories.RoomReservationRepository;
import nl.tudelft.oopp.cars.repositories.UserRepository;
import nl.tudelft.oopp.shared.requests.read.GetReservationsRequest;
import nl.tudelft.oopp.shared.responses.content.BikeReservationResponse;
import nl.tudelft.oopp.shared.responses.content.ReservationsResponse;
import nl.tudelft.oopp.shared.responses.content.RoomReservationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ReservationsController {

    Logger logger = LoggerFactory.getLogger(ReservationsController.class);

    @Autowired
    private RoomReservationRepository roomReservationRepository;

    @Autowired
    private BikeReservationRepository bikeReservationRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Gets a list of all of the reservations of a specific user.
     *
     * @param json JSON string with the email of the user whose reservations you are trying to find
     * @return List of all the users reservations
     */
    @PostMapping(path = "getReservations")
    public @ResponseBody
    String getReservations(@RequestBody String json, HttpServletRequest req,
                           HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();
        GetReservationsRequest request = gson.fromJson(json, GetReservationsRequest.class);

        logger.debug("Getting current reservations for user {}", request.getEmail());
        List<RoomReservation> allRoomReservations =
                roomReservationRepository.getByUser(request.getEmail());
        List<BikeReservation> allBikeReservations =
                bikeReservationRepository.getByUser(request.getEmail());

        Optional<User> u = userRepository.findById(request.getEmail());
        User user = u.get();
        if (user.getRole().equals("admin") || request.getEmail().equals("all")) {
            allRoomReservations = roomReservationRepository.getAllReservation();
            allBikeReservations = bikeReservationRepository.findAllFromBikeReservation();
        }

        List<RoomReservation> roomReservations = new ArrayList<RoomReservation>();

        roomReservations.addAll(getReservationsRooms(
                request.getPreviousCurrent(), allRoomReservations));

        List<RoomReservationResponse> roomReservationResponse =
                new ArrayList<RoomReservationResponse>();
        for (RoomReservation reservationToResponse : roomReservations) {

            roomReservationResponse.add(new RoomReservationResponse(reservationToResponse.getId(),
                    reservationToResponse.getDate(), reservationToResponse.getTimeslot(),
                    reservationToResponse.getUser(), reservationToResponse.getFaculty(),
                    reservationToResponse.getBuilding(), reservationToResponse.getRoom()));

        }

        List<BikeReservation> bikeReservations = new ArrayList<BikeReservation>();

        bikeReservations.addAll(getReservationsBikes(
                request.getPreviousCurrent(), allBikeReservations));

        List<BikeReservationResponse> bikeReservationResponse =
                new ArrayList<BikeReservationResponse>();

        for (BikeReservation reservationToResponse : bikeReservations) {

            bikeReservationResponse.add(new BikeReservationResponse(reservationToResponse.getId(),
                    reservationToResponse.getUser(), reservationToResponse.getFromFaculty(),
                    reservationToResponse.getToFaculty(), reservationToResponse.getPickup(),
                    reservationToResponse.getDropOff()));

        }

        ReservationsResponse reservationsResponse = new
                ReservationsResponse(bikeReservationResponse, roomReservationResponse);

        return gson.toJson(reservationsResponse);
    }


    /**
     * Gets all current/previous reservations for rooms.
     * tried to use sql query but had to change type date to int (eg.13032020)
     * @param  option choose active reservation or previous reservation
     * @return JSON file containing the list of all active reservations
     */
    public static List<RoomReservation>
        getReservationsRooms(String option, List<RoomReservation> activeReservations) {
        Calendar now = Calendar.getInstance();

        List<RoomReservation> currentActiveReservations = new ArrayList<RoomReservation>();
        List<RoomReservation> previousReservations = new ArrayList<RoomReservation>();

        for (RoomReservation activeReservation : activeReservations) {

            Calendar calendar = activeReservation.getDate();
            String str = activeReservation.getTimeslot();
            String[] str2 = str.split("-");
            String[] str3 = str2[1].split(":");
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(str3[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(str3[1]));

            if (calendar.getTime().after(now.getTime())) {
                currentActiveReservations.add(activeReservation);
            } else {
                previousReservations.add(activeReservation);
            }
        }
        if (option.equals("active")) {
            return currentActiveReservations;
        } else {
            return previousReservations;
        }
    }

    /**
     * Gets all current/previous reservations for bikes.
     * tried to use sql query but had to change type date to int (eg.13032020)
     * @param  option choose active reservation or previous reservation
     * @return JSON file containing the list of all active reservations
     */
    public static List<BikeReservation>
        getReservationsBikes(String option, List<BikeReservation> allReservations) {
        Calendar now = Calendar.getInstance();

        List<BikeReservation> currentActiveReservations = new ArrayList<BikeReservation>();
        List<BikeReservation> previousReservations = new ArrayList<BikeReservation>();

        for (BikeReservation activeReservation : allReservations) {
            Calendar calendar = activeReservation.getDropOff();

            calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR));
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));

            if (calendar.getTime().after(now.getTime())) {
                currentActiveReservations.add(activeReservation);
            } else {
                previousReservations.add(activeReservation);
            }
        }
        if (option.equals("active")) {
            return currentActiveReservations;
        } else {
            return previousReservations;
        }
    }

}


