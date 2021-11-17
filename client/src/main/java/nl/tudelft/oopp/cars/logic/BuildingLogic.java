package nl.tudelft.oopp.cars.logic;

import java.util.Arrays;
import java.util.List;
import javafx.collections.ObservableList;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import nl.tudelft.oopp.shared.responses.content.BuildingResponse;
import nl.tudelft.oopp.shared.responses.content.BuildingsResponse;

public class BuildingLogic {

    /**
     * Parses the AllBuildingsResponse into an observable list of user-friendly strings.
     * @param list the observable list to add the strings into
     * @param buildings the AllBuildingsResponse to get the information from
     */
    public static void parseResponse(ObservableList<String> list,
                                     BuildingsResponse buildings) {
        for (BuildingResponse building : buildings.getBuildings()) {
            if (building == null) {
                continue;
            }
            String roomString = String.format("Building %d: %s",
                    building.getId(), building.getName());
            list.add(roomString);
        }
    }

    /**
     * Parses the AllBuildingsResponse into an observable list of user-friendly strings.
     * @param list the observable list to add the strings into
     * @param buildings the AllBuildingsResponse to get the information from
     */
    public static void parseAdminResponse(ObservableList<String> list,
                                          BuildingsResponse buildings) {
        for (BuildingResponse building : buildings.getBuildings()) {
            if (building == null) {
                continue;
            }
            String roomString = String.format("Building %d: %s %s, located at %s",
                    building.getId(), "(" + building.getFaculty() + ")", building.getName(),
                    building.getAddress());
            list.add(roomString);
        }
    }

    /**
     * Parses the given string to return the building id.
     * @param s the string containing the building information
     * @return the id of the building
     */
    public static int parseStringId(String s) {
        return Integer.parseInt(s.split(" ")[1].split(":")[0]);
    }

    /**
     * Parses the given string to return the building name.
     * @param s the string containing the building information
     * @return the name of the building
     */
    public static String parseStringName(String s) {
        return s.split(": ")[1];
    }

    /**
     * Logic for sorting buildings.
     * @param buildings list of buildings
     * @param std 1: sort by id, 2: sort by name
     * @param opt 1: ascending 2: descending
     * @return sorted list of all buildings that are displayed
     */
    public static BuildingsResponse sortingBuildingOption(
            BuildingsResponse buildings, int std, int opt) {

        List<BuildingResponse> list = buildings.getBuildings();

        List<BuildingResponse> sortedB;
        if (std == 2) {
            sortedB = BuildingLogic.sortById(list, opt);
        } else {
            sortedB = BuildingLogic.sortByName(list, opt);
        }
        return new BuildingsResponse(sortedB);
    }

    /**
     * Logic for sorting buildings depending on their name.
     * @param buildings list of buildings
     * @param option 1: ascending 2: descending
     * @return sorted list of all buildings that are displayed
     */
    public static List<BuildingResponse> sortByName(
            List<BuildingResponse> buildings, int option) {
        int i = 1;
        if (option == 2) {
            i = -1;
        }
        int finalI = i;

        buildings.sort((a1, a2) -> {
            if (a1.getName().compareTo(a2.getName()) > 0) {
                return finalI;
            } else if (a1.getName().compareTo(a2.getName()) < 0) {
                return -1 * finalI;
            }
            return 0;
        });
        return buildings;
    }

    /**
     * Logic for sorting buildings depending on their number.
     * @param buildings list of buildings
     * @param option 1: ascending 2: descending
     * @return sorted list of all buildings that are displayed
     */
    public static List<BuildingResponse> sortById(
            List<BuildingResponse> buildings, int option) {
        int i = 1;
        if (option == 2) {
            i = -1;
        }
        int finalI = i;

        buildings.sort((a1, a2) -> {
            if (a1.getId() > a2.getId()) {
                return finalI;
            } else if (a1.getId() < a2.getId()) {
                return -1 * finalI;
            }
            return 0;
        });
        return buildings;
    }

    /**
     * Logic for checking empty field.
     * @param idInString id of building
     * @param name  name of building
     * @param address address of building
     * @param faculty faculty of building
     * @return list of set text for the fields
     */
    public static String[] checkEmptyField(String idInString,
                                           String name, String address, String faculty) {

        String[] msg = new String[5];
        Arrays.fill(msg, "");

        if (idInString == null || idInString.equals("") || idInString.equals("0000")) {
            msg[0] = "0000";
            msg[4] = "false";
        } else {
            msg[0] = idInString;
        }

        if (name == null || name.equals("") || name.equals("field is empty. not saved")) {
            msg[1] = "field is empty. not saved";
            msg[4] = "false";
        } else {
            msg[1] = name;
        }

        if (address == null || address.equals("") || address.equals("field is empty. not saved")) {
            msg[2] = "field is empty. not saved";
            msg[4] = "false";
        } else {
            msg[2] = address;
        }

        if (faculty == null || faculty.equals("") || faculty.equals("field is empty. not saved")) {
            msg[3] = "field is empty. not saved";
            msg[4] = "false";
        } else {
            msg[3] = faculty;
        }
        if (!msg[4].equals("false")) {
            Arrays.fill(msg, "");
        }
        return msg;

    }

    /**
     * Logic for getting building id to be deleted.
     * @param buildingToDelete building to be deleted
     * @return list of set text for the fields
     */
    public static int getBuildingIdToDelete(String buildingToDelete) {
        String[] split1 = buildingToDelete.split(" ");
        String[] split2 = split1[1].split(":");

        return Integer.parseInt(split2[0]);
    }


}
