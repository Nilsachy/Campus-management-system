package nl.tudelft.oopp.cars.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.tudelft.oopp.cars.constants.StringResponses;
import nl.tudelft.oopp.cars.entities.Announcement;
import nl.tudelft.oopp.cars.repositories.AnnouncementRepository;
import nl.tudelft.oopp.shared.requests.create.CreateAnnouncementRequest;
import nl.tudelft.oopp.shared.requests.delete.RemoveAnnouncementRequest;
import nl.tudelft.oopp.shared.requests.read.GetByDateRequest;
import nl.tudelft.oopp.shared.requests.read.GetByIdRequest;
import nl.tudelft.oopp.shared.responses.content.AnnouncementResponse;
import nl.tudelft.oopp.shared.responses.content.AnnouncementsResponse;

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
public class AnnouncementController {

    @Autowired
    AnnouncementRepository repository;

    Logger logger = LoggerFactory.getLogger(BikeStorageController.class);

    /**
     * Create - adds announcement to DB.
     * @param json - json object of addAnnouncementRequest
     * @return empty string
     */
    @PostMapping(path = "addAnnouncement")
    public @ResponseBody String addAnnouncement(@RequestBody String json,
                                                HttpServletRequest req, HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        if (!req.getSession(false).getAttribute("role").equals("admin")) {
            return StringResponses.ONLY_ADMINS;
        }

        Gson gson = new GsonBuilder().create();

        CreateAnnouncementRequest request = gson.fromJson(json, CreateAnnouncementRequest.class);

        Announcement a = new Announcement(request.getPosted(), request.getRelevantUntil(),
                request.getTitle(), request.getContent(), request.getUser());
        repository.save(a);
        logger.debug("Saved announcement: {}", a.toString());

        return "Announcement saved.";
    }

    /**
     * Returns all announcements in the DB.
     * @return JSON object AnnouncementsResponse
     */
    @GetMapping("getAllAnnouncements")
    public @ResponseBody String getAllAnnouncements(HttpServletRequest req,
                                                    HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        List<Announcement> result = repository.findAll();

        logger.debug("Returning all {} announcements.", result.size());

        List<AnnouncementResponse> announcements = new ArrayList<>();
        for (Announcement a : result) {
            announcements.add(new AnnouncementResponse(a.getId(), a.getPosted(),
                    a.getRelevantUntil(), a.getTitle(), a.getContent(), a.getUser()));
        }

        AnnouncementsResponse response = new AnnouncementsResponse(announcements);
        Gson gson = new GsonBuilder().create();
        return gson.toJson(response);
    }

    /**
     * Returns all announcements that are relevant on the date in the parameter.
     * @param json - json object of GetByDateRequest
     * @return json string of a AnnouncementsResponse which is a list of all relevant announcements.
     */
    @PostMapping(path = "getAnnouncementsByDate")
    public @ResponseBody String getByDate(@RequestBody String json,
                                          HttpServletRequest req, HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();
        GetByDateRequest request = gson.fromJson(json, GetByDateRequest.class);

        List<Announcement> result = repository.getByDate(request.getDate());
        List<AnnouncementResponse> responseList = new ArrayList<>();

        for (Announcement a : result) {
            responseList.add(new AnnouncementResponse(a.getId(), a.getPosted(),
                    a.getRelevantUntil(), a.getTitle(), a.getContent(), a.getUser()));
        }

        AnnouncementsResponse response = new AnnouncementsResponse(responseList);

        return gson.toJson(response);
    }

    /**
     * Removes record of Announcement from the DB, if present.
     * @param json - json object representation of a RemoveAnnouncementRequest object, containing
     *             the id of the to be deleted record
     * @return success/failure message
     */
    @DeleteMapping(path = "deleteAnnouncement")
    public @ResponseBody String removeAnnouncement(@RequestBody String json, HttpServletRequest req,
                                                   HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        if (!req.getSession(false).getAttribute("role").equals("admin")) {
            return StringResponses.ONLY_ADMINS;
        }

        Gson gson = new GsonBuilder().create();

        RemoveAnnouncementRequest request = gson.fromJson(json, RemoveAnnouncementRequest.class);

        Optional<Announcement> a = repository.findById(request.getId());

        if (a.isEmpty()) {
            logger.debug("Could not remove announcement with id '{}' from database",
                    request.getId());
            return "Announcement not deleted from database.";
        } else {

            repository.deleteById(request.getId());
            // repository.deleteById(a.get().getId());
            //repository.removeById(request.getId());
            //repository.removeById(a.get().getId());
        }
        logger.debug("Removed announcement with id '{}' from database", request.getId());
        return "Announcement deleted.";
    }

    /**
     * Getter - returns the announcement with a certain ID, if present in the DB.
     * @param json - json object of GetByIdRequest
     * @return Response object for announcement with the found announcement, if found.
     */
    @PostMapping(path = "getAnnouncementById")
    public @ResponseBody String getAnnouncementById(@RequestBody String json,
                                                    HttpServletRequest req,
                                                    HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();
        GetByIdRequest request = gson.fromJson(json, GetByIdRequest.class);

        logger.debug("Returning announcement with id '{}", request.getId());

        Optional<Announcement> a = repository.findById(request.getId());

        if (a.isEmpty()) {
            logger.debug("No announcement in DB with this id");
            return null;
        }

        Announcement an = a.get();
        logger.debug("Found announcement with ID '{}'", an.getId());

        AnnouncementResponse response = new AnnouncementResponse(an.getId(), an.getPosted(),
                an.getRelevantUntil(), an.getTitle(), an.getContent(), an.getUser());
        return gson.toJson(response);
    }

    /**
     * Deletes all no-longer-relevant announcements from the database.
     * @return Success/failure message
     */
    @DeleteMapping(path = "deleteNonRelevantAnnouncements")
    public @ResponseBody String deleteNotRelevant(HttpServletRequest req, HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        logger.debug("Deleting all records in DB with relevant_until date before current date");

        Calendar c = Calendar.getInstance();

        try {
            repository.deleteNotRelevant(c);

        } catch (Exception e) {
            logger.debug("Failed to delete all records with relevant_until date before {}",
                    c.toString());
            return "Failed to delete non-relevant announcements";
        }

        logger.debug("Succeeded to delete all records with relevant_until date before {}",
                c.toString());
        return "Deleted all no longer relevant announcements";
    }

}
