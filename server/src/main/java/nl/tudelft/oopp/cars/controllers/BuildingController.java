package nl.tudelft.oopp.cars.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.tudelft.oopp.cars.constants.StringResponses;
import nl.tudelft.oopp.cars.entities.Building;
import nl.tudelft.oopp.cars.repositories.BuildingRepository;
import nl.tudelft.oopp.shared.requests.create.AddBuildingRequest;
import nl.tudelft.oopp.shared.requests.delete.RemoveBuildingRequest;
import nl.tudelft.oopp.shared.requests.read.GetByAddressRequest;
import nl.tudelft.oopp.shared.requests.read.GetByBuildingIdRequest;
import nl.tudelft.oopp.shared.requests.read.GetByFacultiesRequest;
import nl.tudelft.oopp.shared.requests.read.GetByFacultyRequest;
import nl.tudelft.oopp.shared.requests.read.GetByNameRequest;
import nl.tudelft.oopp.shared.responses.content.BuildingResponse;
import nl.tudelft.oopp.shared.responses.content.BuildingsResponse;

import org.h2.util.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BuildingController {

    Logger logger = LoggerFactory.getLogger(BuildingController.class);

    @Autowired
    BuildingRepository repository;

    /**
     * Adds a new building to the database.
     * @param json JSON string containing all the parameters for the new building
     * @return Success/failure message
     */
    @PostMapping(path = "addBuilding")
    public @ResponseBody String addBuilding(@RequestBody String json,
                                            HttpServletRequest req, HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        if (!req.getSession(false).getAttribute("role").equals("admin")) {
            return StringResponses.ONLY_ADMINS;
        }

        Gson gson = new GsonBuilder().create();
        AddBuildingRequest request = gson.fromJson(json, AddBuildingRequest.class);

        Optional<Building> building = repository.findById(request.getId());
        Building newBuilding = new Building(request.getId(), request.getName(),
                request.getAddress(), request.getFaculty());

        if (building.isEmpty()) {
            // repository.saveBuilding(building);
            logger.debug("Added building {} to the database",
                    newBuilding.toString());
        } else {
            // repository.updateBuilding(building);
            logger.debug("Updated the value of building {} in the database",
                    newBuilding.toString());
        }

        repository.save(newBuilding); // creates and updates
        return "Building saved.";
    }


    /**
     * Removes a building from the database.
     * @param json Json string that contains the ID of the building that needs to be removed,
     *             which is inconveniently named 'name' in the request class.
     * @return Response for the client, about the status of the request
     */
    @PostMapping(path = "removeBuilding")
    public @ResponseBody String removeBuilding(@RequestBody String json,
                                               HttpServletRequest req, HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        if (!req.getSession(false).getAttribute("role").equals("admin")) {
            return StringResponses.ONLY_ADMINS;
        }

        Gson gson = new GsonBuilder().create();

        RemoveBuildingRequest request =
                gson.fromJson(json, RemoveBuildingRequest.class);

        Optional<Building> building = repository.findById(request.getName());

        if (building.isEmpty()) {
            logger.debug("Could not remove building {} from database", request.getName());
            return "Building not deleted from database.";
        }

        repository.deleteById(request.getName());
        logger.debug("Removed building '{}' from database", request.getName());
        return "Building deleted.";
    }

    /**
     * Gets list of all Buildings stored in the database.
     * @return List of all buildings in the database
     */
    @GetMapping(path = "getAllBuildings")
    public @ResponseBody String getAllBuildings(HttpServletRequest req, HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        List<Building> result = repository.findAll();

        logger.debug("Getting all {} buildings", result.size());

        return gson.toJson(buildingListToResponse(result));
    }

    /**
     *  Gets a building for a given id.
     * @param json JSON string containing id
     * @return JSON string of the building whose id was sent, error string if none was found
     */
    @PostMapping("getBuildingById")
    public @ResponseBody String getById(@RequestBody String json, HttpServletRequest req,
                                        HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        GetByBuildingIdRequest request = gson.fromJson(json, GetByBuildingIdRequest.class);

        logger.debug("Getting a building for id {}", request.getBuildingId());

        Optional<Building> result = repository.findById(request.getBuildingId());

        if (result.isEmpty()) {
            logger.debug("Could not find a building for id {}", request.getBuildingId());
            return "ERROR no building found";
        } else {
            logger.debug("Found a building for id {}", request.getBuildingId());
            return gson.toJson(buildingToResponse(result.get()));
        }
    }

    /**
     * Gets a building for a given address.
     * @param json JSON string containing address
     * @return JSON string of the building whose address was sent, error string if none found
     */
    @PostMapping("getBuildingByAddress")
    public @ResponseBody String getByAddress(@RequestBody String json, HttpServletRequest req,
                                             HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        GetByAddressRequest request = gson.fromJson(json, GetByAddressRequest.class);

        logger.debug("Getting a building for address {}", request.getAddress());

        List<Building> result = repository.findByAddress(request.getAddress());

        if (result.isEmpty()) {
            logger.debug("Could not find a building for address {}", request.getAddress());
            return "ERROR no building found";
        }
        logger.debug("Found a building for address {}", request.getAddress());


        return gson.toJson(result.get(0));
    }

    /**
     * Get information of the named building.
     * @param json JSON string containing the building name
     * @return JSON string of Building that has the given name,
     *          if no building was found: error string
     */
    @PostMapping("getBuildingByName")
    public @ResponseBody String getByName(@RequestBody String json, HttpServletRequest req,
                                          HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        GetByNameRequest request = gson.fromJson(json, GetByNameRequest.class);

        List<Building> result = repository.findByName(request.getName());
        logger.debug("Getting a building for name {}", request.getName());
        if (result.isEmpty()) {
            logger.debug("Could not find a building for name {}", request.getName());
            return "ERROR no building found";
        }
        logger.debug("Found a building for name {}", request.getName());

        return gson.toJson(buildingListToResponse(result));
    }

    /**
     * Get buildings that are part of the given faculty.
     * @param json JSON string containing the faculty name
     * @return JSON string List of Building's that are part of the given faculty,
     *          error string if none found
     */
    @PostMapping("getBuildingsByFaculty")
    public @ResponseBody String getByFaculty(@RequestBody String json, HttpServletRequest req,
                                             HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        GetByFacultyRequest request = gson.fromJson(json, GetByFacultyRequest.class);

        List<Building> result = repository.findByFaculty(request.getFaculty());

        logger.debug("Getting buildings for faculty {}", request.getFaculty());

        if (result.isEmpty()) {
            logger.debug("Could not find any buildings for faculty {}", request.getFaculty());
            return  "ERROR no buildings found";
        }

        logger.debug("Found a/some building(s) for faculty {}", request.getFaculty());

        return gson.toJson(buildingListToResponse(result));
    }

    /**
     * Get buildings that are part of the filtering faculties.
     * @param json JSON string with the faculties the returned buildings are a part of
     * @return JSON string List of Building's that are part of the given faculties
     */
    @PostMapping("getBuildingsByFilters")
    public @ResponseBody String getByFaculties(@RequestBody String json, HttpServletRequest req,
                                               HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        GetByFacultiesRequest request = gson.fromJson(json, GetByFacultiesRequest.class);

        List<Building> result = repository.findAll();

        result.removeIf(p -> !request.getFaculties().contains(p.getFaculty()));

        return gson.toJson(buildingListToResponse(result));
    }

    /**
     * Gets the image of the building as a jpeg/byte-array.
     * @param name name of the building and the jpeg file
     * @throws IOException when the image file isn't found
     * @return Byte Array for a JPEG image of the requested building.
     */
    @GetMapping(value = "/image_building/{name}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getBuildingImage(@PathVariable String name) throws IOException {

        logger.debug("Getting an image for building {}", name);
        // we dont have the images yet
        InputStream in = getClass().getResourceAsStream("/images/" + name);
        //return IOUtils.toByteArray(in);
        return IOUtils.readBytesAndClose(in, -1);
        // I don't know if this is what we wanted, and at this point I'm too afraid to ask.
    }

    /**
     * Turns a list of buildings into a response object.
     *
     * @param buildings List of building entities
     * @return A response object
     */
    private BuildingsResponse buildingListToResponse(List<Building> buildings) {
        List<BuildingResponse> buildingResponses = new ArrayList<>();

        for (Building building : buildings) {
            buildingResponses.add(buildingToResponse(building));
        }

        return new BuildingsResponse(buildingResponses);
    }

    /**
     * Turns a building into a user response object.
     *
     * @param building The building entity
     * @return A response object
     */
    private BuildingResponse buildingToResponse(Building building) {
        return new BuildingResponse(
                building.getId(),
                building.getName(),
                building.getAddress(),
                building.getFaculty()
        );
    }
}
