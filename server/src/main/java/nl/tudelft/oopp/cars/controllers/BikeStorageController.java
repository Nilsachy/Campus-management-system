package nl.tudelft.oopp.cars.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.tudelft.oopp.cars.constants.StringResponses;
import nl.tudelft.oopp.cars.entities.BikeStorage;
import nl.tudelft.oopp.cars.repositories.BikeStorageRepository;
import nl.tudelft.oopp.shared.requests.create.AddBikeStorageRequest;
import nl.tudelft.oopp.shared.requests.delete.RemoveBikeStorageByFacultyRequest;
import nl.tudelft.oopp.shared.requests.read.GetByFacultyRequest;

import nl.tudelft.oopp.shared.responses.content.AvailableBikeStoragesResponse;
import nl.tudelft.oopp.shared.responses.content.BikeStorageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BikeStorageController {

    Logger logger = LoggerFactory.getLogger(BikeStorageController.class);

    @Autowired
    private BikeStorageRepository repository;

    /**
     * Adds a bike storage of a faculty to the database.
     *
     * @param json JSON request containing the faculty and capacity
     * @return message saying whether or not the data has been saved to the database.
     */
    @PostMapping(path = "addBikeStorage")
    public @ResponseBody
    String addBikeStorage(@RequestBody String json,
                          HttpServletRequest req, HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        if (!req.getSession(false).getAttribute("role").equals("admin")) {
            return StringResponses.ONLY_ADMINS;
        }

        Gson gson = new GsonBuilder().create();
        AddBikeStorageRequest request = gson.fromJson(json, AddBikeStorageRequest.class);

        Optional<BikeStorage> a = repository.findById(request.getFaculty());
        //        if (!a.isEmpty()) {
        //            logger.debug("Could not add a bike
        //            storage for faculty {}, as it already exists",
        //                    request.getFaculty());
        //            return "Bike storage for this faculty already exists.";
        //        }

        BikeStorage b = new BikeStorage(request.getFaculty(), request.getMaxAvailable());
        repository.save(b);
        logger.debug("Saved a new bike storage: {}", b.toString());

        return "Bike storage saved.";
    }


    /**
     * Removes a bike storage from a faculty.
     *
     * @param json JSON Containing the faculty name
     * @return response to tell if the operation succeeded
     */
    @PostMapping(path = "removeBikeStorage")
    public @ResponseBody
    String removeBikeStorage(@RequestBody String json,
                             HttpServletRequest req, HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        if (!req.getSession(false).getAttribute("role").equals("admin")) {
            return StringResponses.ONLY_ADMINS;
        }

        Gson gson = new GsonBuilder().create();
        RemoveBikeStorageByFacultyRequest request =
                gson.fromJson(json, RemoveBikeStorageByFacultyRequest.class);

        logger.debug("Removing bike storage for faculty {}", request.getFaculty());

        repository.deleteById(request.getFaculty());

        return "Bike Storage removed";
    }


    /**
     * Returns List of all bike storages saved in the Database.
     *
     * @return JSON list of bike storages in database
     */
    @GetMapping(path = "getAllBikeStorages")
    public @ResponseBody
    String getAllBikeStorages(HttpServletRequest req, HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        List<BikeStorage> result = repository.findAll();

        logger.debug("Returning all {} bike storages", result.size());

        List<BikeStorageResponse> availableBikeStoragesResponse = new ArrayList<>();
        for (BikeStorage availableStorages : result) {
            availableBikeStoragesResponse
                    .add(new BikeStorageResponse(
                            availableStorages.getFaculty(), availableStorages.getMaxAvailable()));
        }
        AvailableBikeStoragesResponse response = new
                AvailableBikeStoragesResponse(availableBikeStoragesResponse);
        Gson gson = new GsonBuilder().create();
        return gson.toJson(response);
    }

    /**
     * returns the bike storage of a certain faculty.
     *
     * @param json JSON request containing the faculty we want to get the bike storages for
     * @return the bike storage of that faculty
     */
    @PostMapping(path = "getBikeStorageForFaculty")
    public @ResponseBody
    String getBikeStorageForFaculty(@RequestBody String json, HttpServletRequest req,
                                    HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();
        GetByFacultyRequest request = gson.fromJson(json, GetByFacultyRequest.class);

        Optional<BikeStorage> o = repository.findById(request.getFaculty());
        if (!o.isEmpty()) {
            logger.debug("Found a bike storage for faculty {}", request.getFaculty());
            return gson.toJson(o.get());
        }

        logger.debug("Could not find a bike storage for faculty {}", request.getFaculty());
        return gson.toJson(null);
    }
}
