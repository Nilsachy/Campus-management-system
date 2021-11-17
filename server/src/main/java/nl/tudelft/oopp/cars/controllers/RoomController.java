package nl.tudelft.oopp.cars.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.tudelft.oopp.cars.constants.StringResponses;
import nl.tudelft.oopp.cars.entities.Room;
import nl.tudelft.oopp.cars.entities.RoomReservation;

import nl.tudelft.oopp.cars.repositories.RoomRepository;
import nl.tudelft.oopp.cars.repositories.RoomReservationRepository;

import nl.tudelft.oopp.shared.requests.create.CreateRoomRequest;
import nl.tudelft.oopp.shared.requests.delete.RemoveRoomByIdRequest;
import nl.tudelft.oopp.shared.requests.read.GetAvailableRoomsRequest;
import nl.tudelft.oopp.shared.requests.read.GetByBuildingIdRequest;
import nl.tudelft.oopp.shared.requests.read.GetByFacultyRequest;
import nl.tudelft.oopp.shared.requests.read.GetByRoomCapacityRequest;

import nl.tudelft.oopp.shared.responses.content.RoomResponse;
import nl.tudelft.oopp.shared.responses.content.RoomsResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RoomController {

    Logger logger = LoggerFactory.getLogger(RoomController.class);

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomReservationRepository reservationRepository;

    /**
     * Attempts to save a new room to the database.
     *
     * @param json string with json representation of the Room
     * @return message of whether the room was saved.
     */
    @PostMapping(path = "addNewRoom")
    public @ResponseBody
    String addNewRoom(@RequestBody String json, HttpServletRequest req, HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        if (!req.getSession(false).getAttribute("role").equals("admin")) {
            return StringResponses.ONLY_ADMINS;
        }

        logger.info("The JSON string:" + json);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        CreateRoomRequest request = gson.fromJson(json, CreateRoomRequest.class);

        try {
            Room newRoom = new Room(
                    request.getFaculty(),
                    request.getBuilding(),
                    request.getRoom(),
                    request.getCapacity(),
                    request.hasWhiteboard(),
                    request.isStaffOnly()
            );

            roomRepository.save(newRoom);

            logger.info("Created and saved a new room: {}",
                    newRoom.toString()
            );

            return "Room saved";
        } catch (Exception ex) {
            logger.error("Error happened while creating room object: {}", ex.toString());
        }

        return "Room could not be saved";

    }

    /**
     * DOES NOT WORK YET.
     * Removes a room from the database.
     *
     * @param json String with the ID of the room you want to remove
     * @return success/failure message
     */
    @PostMapping(path = "removeRoom")
    public @ResponseBody
    String removeRoom(@RequestBody String json, HttpServletRequest req, HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        if (!req.getSession(false).getAttribute("role").equals("admin")) {
            return StringResponses.ONLY_ADMINS;
        }

        Gson gson = new GsonBuilder().create();
        RemoveRoomByIdRequest request = gson.fromJson(json, RemoveRoomByIdRequest.class);

        try {
            roomRepository.deleteById((long) request.getRoomId());
        } catch (Exception e) {
            logger.debug("Could not delete room with ID: {}", request.getRoomId());
            return "Not deleted";
        }

        logger.debug("Deleted room with ID: {}", request.getRoomId());

        return "Deleted";
    }

    /**
     * Gives all the rooms for a given building.
     *
     * @param json Json string with the ID of the building for which to request the rooms
     * @return List of rooms belonging to the building
     */
    @PostMapping(path = "getRoomByBuilding")
    public @ResponseBody
    String getRoomByBuilding(@RequestBody String json, HttpServletRequest req,
                             HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        GetByBuildingIdRequest request = gson.fromJson(json, GetByBuildingIdRequest.class);

        List<Room> result = roomRepository.findByBuilding(request.getBuildingId());

        logger.debug("Found {} rooms for building with ID {}",
                result.size(), request.getBuildingId());

        return gson.toJson(roomListToResponse(result));
    }

    /**
     * Gets all the rooms that exist in the database.
     *
     * @return A List of all rooms that exist within the database
     */
    @GetMapping(path = "getAllRooms")
    public @ResponseBody
    String getAllRooms(HttpServletRequest req, HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        List<Room> result = roomRepository.findAll();

        logger.debug("Returning all {} rooms", result.size());

        return gson.toJson(roomListToResponse(result));
    }

    /**
     * Gets all the rooms of a given faculty.
     *
     * @param json Json string containing the name of the faculty for which to request the rooms
     * @return List of all the rooms that belong to the faculty
     */
    @PostMapping(path = "getRoomsByFaculty")
    public @ResponseBody
    String getRoomsByFaculty(@RequestBody String json, HttpServletRequest req,
                             HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        GetByFacultyRequest request = gson.fromJson(json, GetByFacultyRequest.class);

        List<Room> result = roomRepository.findByFaculty(request.getFaculty());

        logger.debug("Returning all {} rooms for faculty {}", result.size(), request.getFaculty());

        return gson.toJson(roomListToResponse(result));
    }

    /**
     * Gives a list of rooms with an exact given capacity.
     *
     * @param json Json containing the desired room capacity (exact)
     * @return List of all rooms with exact capacity.
     */
    @PostMapping(path = "getRoomsByCapacity")
    public @ResponseBody
    String getRoomsByCapacity(@RequestBody String json, HttpServletRequest req,
                              HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        GetByRoomCapacityRequest request = gson.fromJson(json, GetByRoomCapacityRequest.class);

        List<Room> result = roomRepository.findByCapacity(request.getCapacity());

        logger.debug("Returning all {} rooms for specific capacity {}",
                result.size(), request.getCapacity());

        return gson.toJson(roomListToResponse(result));
    }

    /**
     * Gets all rooms with a minimum capacity.
     *
     * @param json Json containing the minimum room capacity
     * @return A list of all rooms with the minimum given capacity or more
     */
    @PostMapping(path = "getRoomsByMinCapacity")
    public @ResponseBody
    String getRoomsByMinCapacity(@RequestBody String json, HttpServletRequest req,
                                 HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        GetByRoomCapacityRequest request = gson.fromJson(json, GetByRoomCapacityRequest.class);

        List<Room> result = roomRepository.findByMinCapacity(request.getCapacity());

        logger.debug("Returning all {} rooms at least capacity {}",
                result.size(), request.getCapacity());

        return gson.toJson(roomListToResponse(result));
    }

    /**
     * Gets all rooms with a whiteboard.
     *
     * @return List of all rooms with a whiteboard
     */
    @GetMapping(path = "getRoomWhiteboard")
    public @ResponseBody
    String getRoomWhiteboard(HttpServletRequest req, HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        List<Room> result = roomRepository.findByWhiteboard();

        logger.debug("Returning all {} rooms a whiteboard", result.size());

        return gson.toJson(roomListToResponse(result));
    }

    /**
     * Gets all rooms that are staff-only.
     *s
     * @return List of all staff-only rooms
     */
    @GetMapping(path = "getRoomsStaffOnly")
    public @ResponseBody
    String getRoomsStaffOnly(HttpServletRequest req, HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        List<Room> result = roomRepository.findByStaffOnly();

        logger.debug("Returning all {} staff-only rooms", result.size());

        return gson.toJson(roomListToResponse(result));
    }

    /**
     * Gets all rooms that are not staff-only.
     *
     * @return List of all rooms that are not staff-only
     */
    @GetMapping(path = "getRoomsNotStaffOnly")
    public @ResponseBody
    String getRoomsNotStaffOnly(HttpServletRequest req, HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        List<Room> result = roomRepository.findByNotStaffOnly();

        logger.debug("Returning all {} non staff-only rooms", result.size());

        return gson.toJson(roomListToResponse(result));
    }

    /**
     * Gets all available rooms for a given date, time and at a given faculty.
     *
     * @param json A json string containing the date, the timeslot and the faculty
     * @return List of all rooms that are open at the given time and date in the given faculty,
     */
    @PostMapping(path = "getAvailableRooms")
    public @ResponseBody
    String getAvailableRooms(@RequestBody String json, HttpServletRequest req,
                             HttpServletResponse res) {

        if (req.getSession(false) == null) {
            return StringResponses.NO_SESSION;
        }

        Gson gson = new GsonBuilder().create();

        GetAvailableRoomsRequest request = gson.fromJson(json,
                GetAvailableRoomsRequest.class);

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        logger.info("Getting all available rooms for  {}, at time {}, "
                        + "at the faculty {}, at {} on {}.",
                request.getCalendar(), request.getTime(),
                request.getBuilding(), request.getBuilding(),request.getTime());

        List<Room> available = roomRepository.findAll();

        List<RoomReservation> existingReservations = reservationRepository.getAllReservation();
        available = filterByBuilding(available, request.getBuilding());
        List<Room> availableRoomsInTimeSlot = getAvailableRoomLogic(
                request,
                available,
                existingReservations
        );

        return gson.toJson(roomListToResponse(availableRoomsInTimeSlot));
    }

    /**
     * Logic for Getting all available rooms for a given date, time and at a given faculty.
     *
     * @param request GetAvailableRoomsRequest request
     * @param available all room
     * @param existingReservations gets all existing reservation
     * @return List of all rooms that are open at the given time and date in the given faculty,
     */
    private static List<Room> getAvailableRoomLogic(GetAvailableRoomsRequest
                       request, List<Room> available, List<RoomReservation> existingReservations) {

        List<Room> availableRoomsInTimeSlot = new ArrayList<Room>();

        for (int i = 0; i < available.size(); i++) {
            int finalI = i;
            if (existingReservations.stream().noneMatch(
                o -> o.getRoom().equals(available.get(finalI).getRoom())
                            && o.getTimeslot().equals(request.getTime())
                            && o.getDate().get(Calendar.DAY_OF_YEAR)
                            == request.getCalendar().get(Calendar.DAY_OF_YEAR)
                            && o.getDate().get(Calendar.YEAR)
                            == request.getCalendar().get(Calendar.YEAR))) {
                availableRoomsInTimeSlot.add(available.get(i));
            }
        }
        return availableRoomsInTimeSlot;
    }



    /**
     * Gets all the rooms of a given faculty from the list of rooms.
     * @param roomList The list of room that will be filtered
     * @param facultyName The name of the faculty for which to request the rooms
     * @return List of all the rooms that belong to the faculty
     */
    private  List<Room> filterByFaculty(List<Room> roomList, String facultyName) {
        List<Room> r = new ArrayList<>();

        roomList.stream()
                .filter(n -> n.getFaculty().equals(facultyName))
                .forEach(r::add);

        return r;
    }

    /**
     * Gets all the rooms of a given building from the list of rooms.
     * @param roomList The list of room that will be filtered
     * @param buildingName The name of the faculty for which to request the rooms
     * @return List of all the rooms that belong to the faculty
     */
    private  List<Room> filterByBuilding(List<Room> roomList, int buildingName) {
        List<Room> r = new ArrayList<>();

        roomList.stream()
                .filter(n -> n.getBuilding() == (buildingName))
                .forEach(r::add);

        return r;
    }

    /**
     * Gets all rooms with a minimum capacity from the list of Rooms.
     * @param roomList The list of room that will be filtered
     * @param capacity The minimum capacity
     * @return A list of all rooms with the minimum given capacity
     */
    private List<Room> filterByMinCapacity(List<Room> roomList, int capacity) {
        List<Room> r = new ArrayList<>();

        roomList.stream()
                .filter(n -> n.getCapacity() >= capacity)
                .forEach(r::add);

        return r;
    }

    /**
     * Filters the list of room regarding whiteboard.
     * @param roomList The list of room that will be filtered
     * @param option option 1 returns room with no white board, option 2 returns rooms
     *               with whiteboard, and option 3 returns all rooms
     * @return List of all staff-only rooms
     */
    private List<Room> filterByWhiteBoard(List<Room> roomList, int option) {
        List<Room> r = new ArrayList<>();

        switch (option) {
            case 1:
                roomList.stream()
                        .filter(n -> !n.hasWhiteboard())
                        .forEach(r::add);
                return r;
            case 2:
                roomList.stream()
                        .filter(n -> n.hasWhiteboard())
                        .forEach(r::add);
                return r;
            default:
                return roomList;
        }
    }

    /**
     * Gets all rooms that are staff-only.
     * @param roomList The list of room that will be filtered
     * @param option option 1 returns for everyone, option 2 returns rooms only for staff,
     *               and option 3 returns all rooms
     * @return List of all staff-only rooms
     */
    private  List<Room> filterByStaffOnly(List<Room> roomList, int option) {

        List<Room> r = new ArrayList<>();

        switch (option) {
            case 1:
                roomList.stream()
                        .filter(n -> !n.isStaffOnly())
                        .forEach(r::add);
                return r;
            case 2:
                roomList.stream()
                        .filter(n -> n.isStaffOnly())
                        .forEach(r::add);
                return r;
            default:
                return roomList;
        }
    }

    /**
     * Turns a list of room entities into a response object.
     *
     * @param rooms List of rooms
     * @return A response object
     */
    private RoomsResponse roomListToResponse(List<Room> rooms) {
        List<RoomResponse> roomResponses = new ArrayList<>();

        for (Room room : rooms) {
            roomResponses.add(roomToResponse(room));
        }

        return new RoomsResponse(roomResponses);
    }

    /**
     * Turns a room entity into a room response object.
     *
     * @param room The room entity
     * @return A response object
     */
    private RoomResponse roomToResponse(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getRoom(),
                room.getBuilding(),
                room.getCapacity(),
                room.hasWhiteboard(),
                room.isStaffOnly()
        );
    }
}