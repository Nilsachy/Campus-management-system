package nl.tudelft.oopp.cars.controllers.logictests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.tudelft.oopp.cars.logic.GeneralLogic;
import nl.tudelft.oopp.cars.logic.RoomLogic;

import nl.tudelft.oopp.shared.responses.content.RoomResponse;
import nl.tudelft.oopp.shared.responses.content.RoomsResponse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RoomLogicTest {

    RoomResponse r0 = new RoomResponse(1, "Project Room 1", 33, 10, true, false);
    RoomResponse r1 = new RoomResponse(2, "Project Room 2", 3, 5, false, true);
    RoomResponse r2 = new RoomResponse(3, "Project Room 3", 13, 20, true, true);
    RoomResponse r3 = new RoomResponse(4, "Project Room 4", 30, 10, false, true);

    @Test
    public void parseResponseTest() {
        RoomsResponse roomsResponse = new RoomsResponse();
        List<RoomResponse> rooms = new ArrayList<>();
        rooms.add(r0);
        rooms.add(r1);
        roomsResponse.setRooms(rooms);

        ObservableList<String> result = FXCollections.observableArrayList();
        ObservableList<String> list = FXCollections.observableArrayList();
        list.add("Project Room 1: fits 10 people and has a whiteboard");
        list.add("Project Room 2: fits 5 people and doesn't have a whiteboard");
        RoomLogic.parseResponse(result, roomsResponse);
        assertEquals(list, result);
    }

    @Test
    public void parseAdminResponseTest() {
        RoomsResponse roomsResponse = new RoomsResponse();
        List<RoomResponse> rooms = new ArrayList<>();
        rooms.add(r0);
        rooms.add(r3);
        roomsResponse.setRooms(rooms);

        ObservableList<String> result = FXCollections.observableArrayList();
        ObservableList<String> list = FXCollections.observableArrayList();
        list.add("Room 1: Project Room 1, located in building 33 with a "
                + "capacity of 10 and a whiteboard ");
        list.add("Room 4: (Staff Only) Project Room 4, located in building 30 "
                + "with a capacity of 10 and no whiteboard ");
        RoomLogic.parseAdminResponse(result, roomsResponse);
        assertEquals(list, result);
    }

    @Test
    public void sortingRoomOptionTest() {
        List<RoomResponse> rooms = new ArrayList<>();
        rooms.add(r0);
        rooms.add(r1);
        rooms.add(r2);
        RoomsResponse roomsResponse = new RoomsResponse();
        roomsResponse.setRooms(rooms);

        List<RoomResponse> roomsResult = new ArrayList<>();
        roomsResult.add(r2);
        roomsResult.add(r0);
        roomsResult.add(r1);
        RoomsResponse roomsCapacity = new RoomsResponse();
        roomsCapacity.setRooms(roomsResult);
        assertEquals(roomsCapacity, RoomLogic.sortingRoomOption(roomsResponse, 2, 2));

        roomsResult.clear();
        roomsResult.add(r1);
        roomsResult.add(r0);
        roomsResult.add(r2);
        assertEquals(roomsCapacity, RoomLogic.sortingRoomOption(roomsResponse, 2, 1));
    }

    @Test
    public void sortByRoomNameTest() {
        List<RoomResponse> rooms = new ArrayList<>();
        rooms.add(r1);
        rooms.add(r2);
        rooms.add(r0);

        List<RoomResponse> ascending = new ArrayList<>();
        ascending.add(r0);
        ascending.add(r1);
        ascending.add(r2);
        assertEquals(ascending, RoomLogic.sortByRoomName(rooms, 1));

        List<RoomResponse> descending = new ArrayList<>();
        descending.add(r2);
        descending.add(r1);
        descending.add(r0);
        assertEquals(descending, RoomLogic.sortByRoomName(rooms, 2));
    }

    @Test
    public void sortByCapacityAscendingTest() {
        List<RoomResponse> rooms = new ArrayList<>();
        rooms.add(r0);
        rooms.add(r1);
        rooms.add(r2);

        List<RoomResponse> descending = new ArrayList<>();
        descending.add(r2);
        descending.add(r0);
        descending.add(r1);
        assertEquals(descending, RoomLogic.sortByCapacity(rooms, 2));
    }

    @Test
    public void sortByCapacityDescendingTest() {
        List<RoomResponse> rooms = new ArrayList<>();
        rooms.add(r0);
        rooms.add(r1);
        rooms.add(r2);

        List<RoomResponse> ascending = new ArrayList<>();
        ascending.add(r1);
        ascending.add(r0);
        ascending.add(r2);
        assertEquals(ascending, RoomLogic.sortByCapacity(rooms, 1));
    }

    @Test
    public void checkEmptyFieldTest() {
        String[] msg = new String[5];
        msg[0] = "field is empty. not saved";
        msg[1] = "0000";
        msg[2] = "field is empty. not saved";
        msg[3] = "0";
        msg[4] = "false";

        String[] result;

        String faculty = "";
        String buildingId = "";
        String room = "field is empty. not saved";
        String capacityInString = "";
        result = RoomLogic.checkEmptyField(faculty, buildingId, room, capacityInString);
        assertEquals(result[0], msg[0]);
        assertEquals(result[1], msg[1]);
        assertEquals(result[2], msg[2]);
        assertEquals(result[3], msg[3]);
        assertEquals(result[4], msg[4]);

        faculty = "Pulse";
        buildingId = "1";
        room = "Project Room";
        capacityInString = "30";

        msg[0] = "";
        msg[1] = "";
        msg[2] = "";
        msg[3] = "";
        msg[4] = "";

        result = RoomLogic.checkEmptyField(faculty, buildingId, room, capacityInString);
        assertEquals(result[0], msg[0]);
        assertEquals(result[1], msg[1]);
        assertEquals(result[2], msg[2]);
        assertEquals(result[3], msg[3]);
        assertEquals(result[4], msg[4]);

        faculty = "Pulse";
        buildingId = "1";
        room = "field is empty. not saved";
        capacityInString = "30";

        msg[0] = faculty;
        msg[1] = buildingId;
        msg[2] = "field is empty. not saved";
        msg[3] = capacityInString;
        msg[4] = "false";

        result = RoomLogic.checkEmptyField(faculty, buildingId, room, capacityInString);
        assertEquals(result[0], msg[0]);
        assertEquals(result[1], msg[1]);
        assertEquals(result[2], msg[2]);
        assertEquals(result[3], msg[3]);
        assertEquals(result[4], msg[4]);
    }

    @Test
    public void getRoomIdToDeleteTest() {
        String room1 = "Project Room 1: fits 10 people and has a whiteboard";
        String room2 = "Room 4: Project Room 4 (Staff Only), located in building 30 with a "
                + "capacity of 10 and no whiteboard";

        int id1 = RoomLogic.getRoomIdToDelete(room1);
        int id2 = RoomLogic.getRoomIdToDelete(room2);

        assertEquals(id1, 1);
        assertEquals(id2, 4);
    }
}
