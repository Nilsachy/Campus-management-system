package nl.tudelft.oopp.cars.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.tudelft.oopp.cars.constants.StringResponses;
import nl.tudelft.oopp.cars.entities.Faculty;
import nl.tudelft.oopp.cars.repositories.FacultyRepository;

import nl.tudelft.oopp.shared.requests.create.CreateFacultyRequest;
import nl.tudelft.oopp.shared.requests.delete.RemoveFacultyRequest;

import nl.tudelft.oopp.shared.requests.read.GetByNameRequest;
import nl.tudelft.oopp.shared.responses.content.FacultiesResponse;
import nl.tudelft.oopp.shared.responses.content.FacultyResponse;
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
public class FacultyController {

    Logger logger = LoggerFactory.getLogger(FacultyController.class);

    @Autowired
    private FacultyRepository repository;

    /**
     * Adds new faculty to the database.
     * @param json - json object in a string
     * @return empty string
     */
    @PostMapping(path = "createFaculty")
    public @ResponseBody String createFaculty(@RequestBody String json,
                                              HttpServletRequest req, HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        if (!req.getSession(false).getAttribute("role").equals("admin")) {
            return StringResponses.ONLY_ADMINS;
        }

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        CreateFacultyRequest request = gson.fromJson(json, CreateFacultyRequest.class);

        Optional<Faculty> fac = repository.findById(request.getId());
        Faculty facNew = new Faculty(request.getId(), request.getName(),
                request.getPhoneNumber());

        if (fac.isEmpty()) {
            logger.debug("Added building {} to the database", facNew.toString());
        } else {
            logger.debug("Updated the value of building {} in the database",
                    facNew.toString());
        }

        repository.save(facNew); // creates and updates
        return "Faculty added or update saved.";
    }

    /**
     * Deletes a faculty from the database, if it exists on the database.
     * @param json json object of request.
     * @return success/failure message
     */
    @DeleteMapping(path = "removeFaculty")
    public @ResponseBody String removeFaculty(@RequestBody String json,
                                              HttpServletRequest req, HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        if (!req.getSession(false).getAttribute("role").equals("admin")) {
            return StringResponses.ONLY_ADMINS;
        }

        Gson gson = new GsonBuilder().create();
        RemoveFacultyRequest request =
                gson.fromJson(json, RemoveFacultyRequest.class);

        try {
            repository.deleteById(request.getId());
        } catch (Exception e) {
            logger.debug("Could not remove faculty {} from database", request.getId());
            return "Faculty not deleted from database.";
        }

        logger.debug("Removed faculty '{}' from database", request.getId());
        return "Faculty deleted.";
    }

    /**
     * Returns a list of all faculties in the database.
     * @return List of all buildings
     */
    @GetMapping(path = "getAllFaculties")
    public @ResponseBody String getAllFaculties(HttpServletRequest req, HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        List<Faculty> resul = repository.findAll();
        logger.debug("Getting all {} faculties", resul.size());
        List<FacultyResponse> result = new ArrayList<>();
        for (Faculty f : resul) {
            result.add(new FacultyResponse(f.getId(), f.getName(), f.getPhone()));
        }
        FacultiesResponse response = new FacultiesResponse(result);
        return (new GsonBuilder()).create().toJson(response);
    }

    /**
     * Returns a faculty with a certain name.
     * @param json - json object of request
     * @return json representation of the Faculty
     */
    @PostMapping(path = "getFacultyByName")
    public @ResponseBody String getFacultyByName(@RequestBody String json, HttpServletRequest req,
                                          HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();
        GetByNameRequest request = gson.fromJson(json, GetByNameRequest.class);

        List<Faculty> result = repository.findByName(request.getName());
        logger.debug("Getting a faculty with name {}", request.getName());

        if (result.isEmpty()) {
            return null;
        }
        return gson.toJson(result.get(0));
    }

}
