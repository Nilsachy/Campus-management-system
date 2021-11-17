package nl.tudelft.oopp.cars.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.tudelft.oopp.cars.constants.StringResponses;
import nl.tudelft.oopp.cars.entities.CustomEvent;
import nl.tudelft.oopp.cars.repositories.CustomEventRepository;
import nl.tudelft.oopp.shared.requests.create.CreateCustomEventRequest;

import nl.tudelft.oopp.shared.requests.delete.RemoveEventByIdRequest;
import nl.tudelft.oopp.shared.requests.read.GetEventsByUserRequest;
import nl.tudelft.oopp.shared.requests.read.GetEventsInSpanRequest;
import nl.tudelft.oopp.shared.requests.update.UpdateCustomEventRequest;
import nl.tudelft.oopp.shared.responses.content.CustomEventResponse;
import nl.tudelft.oopp.shared.responses.content.CustomEventsResponse;

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
public class CustomEventController {

    Logger logger = LoggerFactory.getLogger(CustomEventController.class);

    @Autowired
    private CustomEventRepository repository;

    /**
     * Adds new custom event to the database.
     *
     * @param json - json object of the custom event
     * @return empty string
     */
    @PostMapping(path = "addCustomEvent")
    public @ResponseBody
    String addNewCustomEvent(@RequestBody String json, HttpServletRequest req,
                             HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        CreateCustomEventRequest request = gson.fromJson(json, CreateCustomEventRequest.class);

        logger.info("Adding a custom event for {}", request.toString());

        CustomEvent customEvent = new CustomEvent(request.getUser(), request.getTitle(),
                request.getStart(), request.getEnd(), request.getAddress(),
                request.getDescription());

        repository.save(customEvent);

        return "Custom event saved.";
    }

    /**
     * Returns a list of all current custom events in the DB.
     *
     * @return List of all custom events in the DB
     */
    @GetMapping(path = "getAllCustomEvents")
    public @ResponseBody
    String getAllCustomEvents(HttpServletRequest req, HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        logger.info("Getting all custom events");

        List<CustomEvent> events = repository.findAll();
        Gson gson = new GsonBuilder().create();

        return gson.toJson(eventsListToResponse(events), CustomEventsResponse.class);
    }

    /**
     * Returns list of all custom events in the DB from a certain user.
     *
     * @param json json string containing the users email
     * @return List of events of the user.
     */
    @PostMapping(path = "getCustomEventsByUser")
    public @ResponseBody
    String getCustomEventsByUser(@RequestBody String json, HttpServletRequest req,
                                 HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        GetEventsByUserRequest request = gson.fromJson(json, GetEventsByUserRequest.class);
        logger.info("Getting all custom events of user {}", request.getUser());

        List<CustomEvent> events = repository.findByUser(request.getUser());

        return gson.toJson(eventsListToResponse(events), CustomEventsResponse.class);
    }

    /**
     * Removes a custom event from a user.
     *
     * @param json - json containing the id of the event
     * @return empty string
     */
    @PostMapping(path = "removeCustomEventsById")
    public @ResponseBody String removeById(@RequestBody String json, HttpServletRequest req,
                                           HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();
        RemoveEventByIdRequest request = gson.fromJson(json, RemoveEventByIdRequest.class);

        logger.info("Deleting custom event with id {}", request.getCustomEventId());

        repository.deleteById(request.getCustomEventId());
        return "Deleted event, if present in the DB.";
    }

    /**
     * Updates an event with the given information.
     *
     * @param json - json object with updated values and other user info
     * @return empty string
     */
    @PostMapping(path = "updateCustomEvent")
    public @ResponseBody String updateCustomEvent(@RequestBody String json, HttpServletRequest req,
                                                  HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        UpdateCustomEventRequest request = gson.fromJson(json, UpdateCustomEventRequest.class);

        logger.info("Updating event from user {} with id {}", request.getUser(), request.getId());

        Optional<CustomEvent> match = repository.findById(request.getId());

        if (match.isEmpty() || !match.get().getUser().equals(request.getUser())) {
            return "No event was found matching that user and ID, nothing was updated";
        }

        CustomEvent oldEvent = match.get();

        oldEvent.setDescription(request.getDescription());
        oldEvent.setAddress(request.getAddress());
        oldEvent.setEnd(request.getEnd());
        oldEvent.setStart(request.getStart());
        oldEvent.setTitle(request.getTitle());

        repository.save(oldEvent);

        return "Updated value";
    }

    /**
     * Gets the events for a given user, in a certain timespan.
     *
     * @param json json object containing user, start date, and end date
     * @return a json event containing a list of all events from the timespan
     */
    @PostMapping(path = "getCustomEventsInTimespanByUser")
    public @ResponseBody String getCustomEventsInTimespan(@RequestBody String json,
                                                          HttpServletRequest req,
                                                          HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        GetEventsInSpanRequest request = gson.fromJson(json, GetEventsInSpanRequest.class);

        logger.info("Getting all events for user {}, between {} and {}",
                request.getUser(), request.getStartDate(), request.getEndDate());

        List<CustomEvent> events = new ArrayList<>();

        if (request.getStartDate().compareTo(request.getEndDate()) >= 0) {
            return gson.toJson(events);
        }

        events = repository.findByUser(request.getUser());

        List<CustomEvent> eventsInSpan = events
                .stream()
                .filter(e -> e.getStart().compareTo(request.getStartDate()) >= 0
                        && e.getEnd().compareTo(request.getEndDate()) <= 0)
                .collect(Collectors.toList());

        return gson.toJson(eventsListToResponse(eventsInSpan), CustomEventsResponse.class);
    }


    /**
     * Turns a list of Custom Events into a response object.
     *
     * @param events List of events
     * @return A response object
     */
    private CustomEventsResponse eventsListToResponse(List<CustomEvent> events) {
        List<CustomEventResponse> eventResponses = new ArrayList<>();

        for (CustomEvent event : events) {
            eventResponses.add(new CustomEventResponse(
                    event.getId(),
                    event.getUser(),
                    event.getTitle(),
                    event.getStart(),
                    event.getEnd(),
                    event.getAddress(),
                    event.getDescription()));
        }

        return new CustomEventsResponse(eventResponses);
    }
}
