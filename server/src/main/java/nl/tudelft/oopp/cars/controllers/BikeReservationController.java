package nl.tudelft.oopp.cars.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.tudelft.oopp.cars.constants.StringResponses;
import nl.tudelft.oopp.cars.entities.BikeReservation;
import nl.tudelft.oopp.cars.entities.BikeStorage;
import nl.tudelft.oopp.cars.repositories.BikeReservationRepository;
import nl.tudelft.oopp.cars.repositories.BikeStorageRepository;
import nl.tudelft.oopp.shared.requests.create.CreateBikeRentalRequest;
import nl.tudelft.oopp.shared.requests.delete.DeleteBikeReservationRequest;
import nl.tudelft.oopp.shared.requests.read.GetAvailableBikeStoragesRequest;
import nl.tudelft.oopp.shared.requests.read.GetByEmailRequest;
import nl.tudelft.oopp.shared.requests.read.GetByFacultyRequest;
import nl.tudelft.oopp.shared.responses.content.AvailableBikeStoragesResponse;
import nl.tudelft.oopp.shared.responses.content.BikeReservationResponse;
import nl.tudelft.oopp.shared.responses.content.BikeStorageResponse;
import nl.tudelft.oopp.shared.responses.content.ReservationsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BikeReservationController {

    Logger logger = LoggerFactory.getLogger(BikeReservationController.class);

    @Autowired
    private BikeReservationRepository bikeReservationRepository;

    @Autowired
    private BikeStorageRepository bikeStorageRepository;

    /**
     * Find all the bike reservations for a given user.
     * @param json The JSON request that contains the users email.
     * @return The list of bike reservations for a user in JSON format
     */
    @PostMapping(path = "findBikeReservationByUser")
    public @ResponseBody String findBikeReservationByUser(@RequestBody String json,
                                                          HttpServletRequest req,
                                                          HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();
        GetByEmailRequest request = gson.fromJson(json, GetByEmailRequest.class);

        logger.debug("Finding reservations for user {}", request.getEmail());

        List<BikeReservation> result = bikeReservationRepository.getByUser(request.getEmail());

        logger.debug("Found {} bike reservations for user {}", result.size(), request.getEmail());
        List<BikeReservationResponse> bikeReservationResponses =
                new ArrayList<BikeReservationResponse>();

        for (BikeReservation r: result) {
            bikeReservationResponses.add(new BikeReservationResponse(r.getId(),
                    r.getUser(), r.getFromFaculty(),
                    r.getToFaculty(), r.getPickup(),
                    r.getDropOff()));
        }

        ReservationsResponse reservationsResponse =
                new ReservationsResponse(bikeReservationResponses, null);

        return gson.toJson(reservationsResponse);
    }

    /**
     * Find all the bike reservations at a given faculty.
     * @param json The JSON request that contains the faculty.
     * @return The list of bike reservations at a faculty in JSON format
     */
    @PostMapping(path = "findByFromFaculty")
    public @ResponseBody String findByFromFaculty(@RequestBody String json,
                                                  HttpServletRequest req,
                                                  HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        GetByFacultyRequest request = gson.fromJson(json, GetByFacultyRequest.class);

        logger.debug("Finding reservations for faculty {}", request.getFaculty());

        List<BikeReservation> result = bikeReservationRepository
                .getByFromFaculty(request.getFaculty());

        logger.debug("Found {} bike reservations for faculty {}",
                result.size(), request.getFaculty());

        List<BikeReservationResponse> bikeReservationResponses =
                new ArrayList<BikeReservationResponse>();

        for (BikeReservation r: result) {
            bikeReservationResponses.add(new BikeReservationResponse(r.getId(),
                    r.getUser(), r.getFromFaculty(),
                    r.getToFaculty(), r.getPickup(),
                    r.getDropOff()));
        }

        ReservationsResponse reservationsResponse =
                new ReservationsResponse(bikeReservationResponses, null);

        return gson.toJson(reservationsResponse);
    }

    /**
     * Find all the bike reservations at a given faculty.
     * @param json The JSON request that contains the faculty.
     * @return The list of bike reservations at a faculty in JSON format
     */
    @PostMapping(path = "findByToFaculty")
    public @ResponseBody String findByToFaculty(@RequestBody String json, HttpServletRequest req,
                                                HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        GetByFacultyRequest request = gson.fromJson(json, GetByFacultyRequest.class);

        logger.debug("Finding reservations for faculty {}", request.getFaculty());

        List<BikeReservation> result = bikeReservationRepository
                .getByToFaculty(request.getFaculty());

        logger.debug("Found {} bike reservations for faculty {}",
                result.size(), request.getFaculty());

        List<BikeReservationResponse> bikeReservationResponses =
                new ArrayList<BikeReservationResponse>();

        for (BikeReservation r: result) {
            bikeReservationResponses.add(new BikeReservationResponse(r.getId(),
                    r.getUser(), r.getFromFaculty(),
                    r.getToFaculty(), r.getPickup(),
                    r.getDropOff()));
        }

        ReservationsResponse reservationsResponse =
                new ReservationsResponse(bikeReservationResponses, null);

        return gson.toJson(reservationsResponse);
    }

    /**
     * Reserves a Bike.
     *
     * @param json the json object string of the requested bike
     * @return empty string
     */
    @PostMapping("rentBike")
    public @ResponseBody
    String rentBike(@RequestBody String json, HttpServletRequest req, HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        CreateBikeRentalRequest request =
                gson.fromJson(json, CreateBikeRentalRequest.class);

        logger.debug("Adding a reservation for {}", request.toString());
        
        BikeReservation bikeReservation = new BikeReservation(request.getUser(), 
                    request.getFromFaculty(), request.getToFaculty(),
                    request.getFromDate(), request.getToDate());
        bikeReservationRepository.save(bikeReservation);
        return "Saved.";
    }

    /**
     * Gets all available bike storage.
     * Assumes that bike 30% of maximum capacity is not filled for returned bikes.
     * Assumes that for balance and better service for consumers bike
     * storage will be set to (30:70 = extra space for returned  Bike: bike for use) every day.
     * Json String will be implemented later when the request gets clear
     * @param json A json string containing the date, time and the faculty
     * @return JSON string List of BikeStorages that are open at the given parameters
     */
    @PostMapping(path = "getAvailableBikeStorages")
    public @ResponseBody
    String getAvailableBikeStorages(@RequestBody String json, HttpServletRequest req,
                                    HttpServletResponse res) throws ParseException {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        GetAvailableBikeStoragesRequest request = gson.fromJson(json,
                GetAvailableBikeStoragesRequest.class);


        logger.info("Getting all available bike storage for fromDate {}, at fromTime {}, "
                        + "toDate {}, at toTime {}, ",
                request.getFromDate().get(Calendar.DATE),
                request.getFromDate().get(Calendar.HOUR_OF_DAY),
                request.getToDate().get(Calendar.DATE),
                request.getToDate().get(Calendar.HOUR_OF_DAY));

        List<BikeStorage> allStorage = bikeStorageRepository.findAll();
        List<BikeReservation> allReservation = bikeReservationRepository.findAll();

        List<BikeStorage> availableStorage = BikeReservationController.getListOfAvailableBikes(
                request, allStorage, allReservation);

        List<BikeStorageResponse> availableBikeStoragesResponse = new ArrayList<>();
        for (BikeStorage availableStorages : availableStorage) {
            availableBikeStoragesResponse
                    .add(new BikeStorageResponse(availableStorages.getFaculty(),
                            availableStorages.getMaxAvailable()));
        }
        AvailableBikeStoragesResponse response = new
                AvailableBikeStoragesResponse(availableBikeStoragesResponse);

        return gson.toJson(response);
    }

    /**
     * Gets all available bike storage.
     * Assumes that bike 30% of maximum capacity is not filled for returned bikes.
     * Assumes that for balance and better  service for consumers bike
     * storage will be set to 30:70 for extra space: bike for use.
     * Json String will be implemented later when the request gets clear
     * @param json A json string containing the date, time and the faculty
     * @return JSON string List of BikeStorages that are open at the given parameters
     */
    @PostMapping(path = "getAvailableBikesForReturn")
    public @ResponseBody
    String getAvailableBikesForReturn(@RequestBody String json, HttpServletRequest req,
                                      HttpServletResponse res) throws ParseException {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        GetAvailableBikeStoragesRequest request = gson.fromJson(json,
                GetAvailableBikeStoragesRequest.class);

        logger.info("Getting all available bike storage for fromDate {}, at fromTime {},"
                        + "toDate {}, at toTime {}, ",
                request.getFromDate().get(Calendar.DATE),
                request.getFromDate().get(Calendar.HOUR_OF_DAY),
                request.getToDate().get(Calendar.DATE),
                request.getToDate().get(Calendar.HOUR_OF_DAY));

        List<BikeStorage> allStorage = bikeStorageRepository.findAll();
        List<BikeReservation> allReservation =
                bikeReservationRepository.findAllFromBikeReservation();

        List<BikeStorage> availableStorage =
                BikeReservationController.getListOfAvailableReturnBikes(
                request, allStorage, allReservation);

        List<BikeStorageResponse> availableBikeStoragesResponse = new ArrayList<>();
        for (BikeStorage availableStorages : availableStorage) {
            availableBikeStoragesResponse
                    .add(new BikeStorageResponse(availableStorages.getFaculty(),
                            availableStorages.getMaxAvailable()));
        }
        AvailableBikeStoragesResponse response = new
                AvailableBikeStoragesResponse(availableBikeStoragesResponse);

        return gson.toJson(response);
    }

    /**
     * Gets all reservation with faculty.
     * this is only for junit test
     * this is not a good way to do this
     * will be changed to using query
     * @param json json DeleteBikeReservationRequest
     * @return completion String
     */
    @PostMapping("deleteBikeReservation")
    public @ResponseBody String deleteBikeReservation(@RequestBody String json,
                                                      HttpServletRequest req,
                                                      HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        DeleteBikeReservationRequest request =
                gson.fromJson(json, DeleteBikeReservationRequest.class);

        logger.debug("Adding a reservation for {}", request.toString());
        bikeReservationRepository.deleteById(request.getId());

        return "deleted.";

    }

    /**
     * Gets all available bike storage.
     * @param request A GetAvailableBikeStoragesRequest
     * @param bikeStorages all storage for bikes
     * @param bikeReservations all storage for bikes
     * @return List of BikeStorages that are open at the given time and date in the given faculty,
     */
    public static List<BikeStorage>
        getListOfAvailableBikes(GetAvailableBikeStoragesRequest request,
        List<BikeStorage> bikeStorages, List<BikeReservation> bikeReservations) {

        List<BikeStorage> availableStorage = new ArrayList<BikeStorage>();
        for (int i = 0; i < bikeStorages.size();i++) {
            int totalBike = (int) Math.round(0.7 * bikeStorages.get(i).getMaxAvailable());

            List<BikeReservation> reservationsFromFaculty = BikeReservationController
                    .filterByFromFaculty(bikeReservations, bikeStorages.get(i).getFaculty());
            List<BikeReservation> reservationsToFaculty = BikeReservationController
                    .filterByToFaculty(bikeReservations, bikeStorages.get(i).getFaculty());

            List<BikeReservation> existingReservationsInSameDay = new ArrayList<BikeReservation>();
            List<BikeReservation> returnedBikeInTheSameDay = new ArrayList<BikeReservation>();

            reservationsFromFaculty.stream()
                    .filter(o -> o.getPickup().get(Calendar.DAY_OF_YEAR)
                            == request.getFromDate().get(Calendar.DAY_OF_YEAR)
                            && o.getPickup().get(Calendar.YEAR)
                            == request.getFromDate().get(Calendar.YEAR))
                    .forEach(existingReservationsInSameDay::add);

            reservationsToFaculty.stream()
                    .filter(o -> o.getDropOff().before(request.getFromDate())
                            && o.getDropOff().get(Calendar.DAY_OF_YEAR)
                            == request.getFromDate().get(Calendar.DAY_OF_YEAR)
                            && o.getDropOff().get(Calendar.YEAR)
                            == request.getFromDate().get(Calendar.YEAR))
                    .forEach(returnedBikeInTheSameDay::add);

            totalBike = totalBike - existingReservationsInSameDay.size()
                    + returnedBikeInTheSameDay.size();

            if (totalBike >= 1) {
                availableStorage.add(bikeStorages.get(i));
            }
        }
        return availableStorage;
    }

    /**
     * Gets all available bike storage.
     * @param request A GetAvailableBikeStoragesRequest
     * @param bikeStorages all storage for bikes
     * @param bikeReservations all storage for bikes
     * @return List of BikeStorages that are open at the given time and date in the given faculty,
     */
    public static List<BikeStorage> getListOfAvailableReturnBikes(
            GetAvailableBikeStoragesRequest request, List<BikeStorage> bikeStorages,
            List<BikeReservation> bikeReservations) {

        List<BikeStorage> availableStorage = new ArrayList<BikeStorage>();
        for (int i = 0; i < bikeStorages.size();i++) {
            int totalBike = (int) Math.round(0.7 * bikeStorages.get(i).getMaxAvailable());
            totalBike = bikeStorages.get(i).getMaxAvailable() - totalBike;
            int totalSpace = bikeStorages.get(i).getMaxAvailable() - totalBike;

            List<BikeReservation> reservationsFromFaculty = BikeReservationController
                    .filterByFromFaculty(bikeReservations, bikeStorages.get(i).getFaculty());
            List<BikeReservation> reservationsToFaculty = BikeReservationController
                    .filterByToFaculty(bikeReservations, bikeStorages.get(i).getFaculty());

            List<BikeReservation> existingReservationsInSameDay = new ArrayList<BikeReservation>();
            List<BikeReservation> returnedBikeInTheSameDay = new ArrayList<BikeReservation>();

            // gets the bikes that are already reserved that same day
            reservationsFromFaculty.stream()
                    .filter(o -> o.getDropOff().get(Calendar.DAY_OF_YEAR)
                            == request.getToDate().get(Calendar.DAY_OF_YEAR)
                            && o.getDropOff().get(Calendar.YEAR)
                            == request.getToDate().get(Calendar.YEAR))
                    .forEach(existingReservationsInSameDay::add);

            // gets the bikes that are returned to the faculty before the requested pickup time
            reservationsToFaculty.stream()
                    .filter(o -> o.getPickup().get(Calendar.DAY_OF_YEAR)
                            == request.getToDate().get(Calendar.DAY_OF_YEAR)
                            && o.getPickup().get(Calendar.YEAR)
                            == request.getToDate().get(Calendar.YEAR)
                            && o.getPickup().before(request.getToDate()))
                    .forEach(returnedBikeInTheSameDay::add);

            totalBike = totalBike - existingReservationsInSameDay.size()
                    + returnedBikeInTheSameDay.size();

            if (totalBike >= 1) {
                availableStorage.add(bikeStorages.get(i));
            }
        }
        return availableStorage;
    }

    /**
     * Gets all reservation with faculty.
     * @param bikeReservationListList The list of bike reservation that will be filtered.
     * @param faculty The faculty.
     * @return A list of BikeReservations that came from the given faculty.
     */
    public static List<BikeReservation>
        filterByFromFaculty(List<BikeReservation> bikeReservationListList, String faculty) {

        List<BikeReservation> b = new ArrayList<>();

        bikeReservationListList.stream()
                .filter(n -> n.getFromFaculty().equals(faculty)).forEach(b::add);
        return b;
    }

    /**
     * Gets all reservation with faculty.
     * @param bikeReservationListList The list of bike reservation that will be filtered.
     * @param faculty The faculty.
     * @return A list of BikeReservations that are going to the given faculty.
     */
    public static List<BikeReservation>
        filterByToFaculty(List<BikeReservation> bikeReservationListList, String faculty) {

        List<BikeReservation> b = new ArrayList<>();

        bikeReservationListList.stream()
                .filter(n -> n.getToFaculty().equals(faculty)).forEach(b::add);
        return b;
    }


}
