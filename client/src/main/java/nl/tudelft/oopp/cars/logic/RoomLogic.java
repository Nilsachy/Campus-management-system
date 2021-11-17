package nl.tudelft.oopp.cars.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javafx.collections.ObservableList;

import nl.tudelft.oopp.shared.responses.content.RoomResponse;
import nl.tudelft.oopp.shared.responses.content.RoomsResponse;

public class RoomLogic {

    /**
     * Parses the AvailableRoomsResponse into an observable list of user-friendly strings.
     * @param list the observable list to add the strings into
     * @param availableRooms the AvailableRoomsResponse to get the information from
     */
    public static void parseResponse(ObservableList<String> list,
                                     RoomsResponse availableRooms) {
        for (RoomResponse room : availableRooms.getRooms()) {
            if (room == null) {
                continue;
            }
            String whiteboard = "";
            if (room.isHasWhiteboard()) {
                whiteboard = whiteboard + "has";
            } else {
                whiteboard = whiteboard + "doesn't have";
            }
            String roomString = String.format("%s: fits %d people and " + whiteboard
                    + " a whiteboard", room.getName(), room.getCapacity());
            list.add(roomString);
        }
    }

    /**
     * Parses the AllRoomsResponse into an observable list of user-friendly strings.
     * @param list The observable list to add the strings into
     * @param rooms The AllRoomsResponse to get the information from
     */
    public static void parseAdminResponse(ObservableList<String> list, RoomsResponse rooms) {
        for (RoomResponse room : rooms.getRooms()) {
            if (room == null) {
                continue;
            }
            String staffonly = "";
            if (room.isStaffOnly()) {
                staffonly = staffonly + "(Staff Only) ";
            }
            String whiteboard = "";
            if (room.isHasWhiteboard()) {
                whiteboard = whiteboard + "a";
            } else {
                whiteboard = whiteboard + "no";
            }
            String roomString = String.format("Room %d: %s%s, located in building %s "
                            + "with a capacity of %s and %s whiteboard ", room.getId(),
                    staffonly, room.getName(), room.getBuilding(), room.getCapacity(), whiteboard);
            list.add(roomString);
        }
    }

    /**
     * Logic for sorting the given list of rooms.
     * @param availableRooms list of rooms
     * @param std 1: sort by name, 2: sort by capacity
     * @param opt 1: ascending 2: descending
     * @return sorted list of all rooms that are available
     */
    public static RoomsResponse
        sortingRoomOption(RoomsResponse availableRooms, int std, int opt) {

        List<RoomResponse> available = availableRooms.getRooms();

        List<RoomResponse> sortedRooms = new ArrayList<RoomResponse>();
        if (std == 2) {
            sortedRooms = RoomLogic.sortByCapacity(available, opt);
        } else {
            sortedRooms = RoomLogic.sortByRoomName(available, opt);
        }
        return new RoomsResponse(sortedRooms);
    }

    /**
     * Logic for sorting rooms depending on their name.
     * @param available list of rooms
     * @param option 1: ascending 2: descending
     * @return sorted list of all rooms that are available
     */
    public static List<RoomResponse> sortByRoomName(List<RoomResponse> available, int option) {
        int i = -1;
        if (option == 2) {
            i = 1;
        }
        int finalI = i;
        available.sort(new Comparator<RoomResponse>() {
            @Override
            public int compare(RoomResponse r1, RoomResponse r2) {
                if (r1.getName().compareTo(r2.getName()) > 0) {
                    return -1 * finalI;
                } else if (r1.getName().compareTo(r2.getName()) < 0) {
                    return finalI;
                }
                return 0;
            }
        });
        return available;
    }

    /**
     * Logic for sorting rooms depending on their capacity.
     * @param available list of rooms
     * @param option 1: ascending 2: descending
     * @return sorted list of all rooms that are available
     */
    public static List<RoomResponse> sortByCapacity(List<RoomResponse> available, int option) {
        int i = 1;
        if (option == 2) {
            i = -1;
        }
        int finalI = i;
        available.sort(new Comparator<RoomResponse>() {
            @Override
            public int compare(RoomResponse r1, RoomResponse r2) {
                if (r1.getCapacity() < r2.getCapacity()) {
                    return -1 * finalI;
                } else if (r1.getCapacity() > r2.getCapacity()) {
                    return finalI;
                }
                return 0;
            }
        });
        return available;
    }

    /**
     * Logic for checking empty field.
     * @param faculty faculty of room
     * @param buildingId  buildingId of room
     * @param room room of room
     * @param capacityInString capacityInString of room
     * @return list of set text for the fields
     */
    public static String[] checkEmptyField(
            String faculty, String buildingId, String room, String capacityInString) {

        String[] msg = new String[5];
        Arrays.fill(msg, "");

        if (faculty == null || faculty.equals("") || faculty.equals("field is empty. not saved")) {
            msg[0] = "field is empty. not saved";
            msg[4] = "false";
        } else {
            msg[0] = faculty;
        }

        if (buildingId == null || buildingId.equals("") || buildingId.equals("0000")) {
            msg[1] = "0000";
            msg[4] = "false";
        } else {
            msg[1] = buildingId;
        }

        if (room == null || room.equals("") || room.equals("field is empty. not saved")) {
            msg[2] = "field is empty. not saved";
            msg[4] = "false";
        } else {
            msg[2] = room;
        }

        if (capacityInString == null || capacityInString.equals("")) {
            msg[3] = "0";
            msg[4] = "false";
        } else {
            msg[3] = capacityInString;
        }

        if (!msg[4].equals("false")) {
            Arrays.fill(msg, "");
        }
        return msg;
    }

    /**
     * Logic for getting room id to be deleted.
     * @param roomToDelete room to be deleted
     * @return list of set text for the fields
     */
    public static int getRoomIdToDelete(String roomToDelete) {
        String[] split1 = roomToDelete.split("Room ");
        String[] split2 = split1[1].split(":");
        return Integer.parseInt(split2[0]);
    }

    /**
     * Logic for getting room to be added.
     * @param room room reservation to be added
     * @return list of set text for the fields
     */
    public static String getRoomNameToAddReservation(String room) {
        String[] split1 = room.split(":");
        return split1[0];
    }

    /**
     * Logic for getting room to be added.
     * @param building room reservation to be added
     * @return list of set text for the fields
     */
    public static String getBuildingNameForRoomReservation(String building) {
        String[] split1 = building.split(": ");
        return split1[1];
    }
}
