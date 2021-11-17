package nl.tudelft.oopp.cars.controllers.logictests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.tudelft.oopp.cars.logic.BuildingLogic;

import nl.tudelft.oopp.shared.responses.content.BuildingResponse;
import nl.tudelft.oopp.shared.responses.content.BuildingsResponse;

import org.junit.jupiter.api.Test;

public class BuildingLogicTest {

    BuildingResponse b0 = new BuildingResponse(33, "EEMCS", "Drebbelweg", "EWI");
    BuildingResponse b1 = new BuildingResponse(13, "Pulse", "Mekelweg", "General");
    BuildingResponse b2 = new BuildingResponse(1, "Aula", "Mekelweg", "General");


    @Test
    public void parseResponseTest() {
        BuildingsResponse buildingsResponse = new BuildingsResponse();
        List<BuildingResponse> buildings = new ArrayList<>();
        buildings.add(b0);
        buildings.add(b1);
        buildingsResponse.setBuildings(buildings);

        ObservableList<String> result = FXCollections.observableArrayList();
        ObservableList<String> list = FXCollections.observableArrayList();
        list.add("Building 33: EEMCS");
        list.add("Building 13: Pulse");
        BuildingLogic.parseResponse(result, buildingsResponse);
        assertEquals(list, result);
    }

    @Test
    public void parseStringIdTest() {
        assertEquals(33, BuildingLogic.parseStringId("Building 33: EEMCS"));
        assertEquals(13, BuildingLogic.parseStringId("Building 13: Pulse"));
    }

    @Test
    public void parseStringNameTest() {
        assertEquals("EEMCS", BuildingLogic.parseStringName("Building 33: EEMCS"));
        assertEquals("Pulse", BuildingLogic.parseStringName("Building 13: Pulse"));
    }

    @Test
    public void sortByNameTest() {
        List<BuildingResponse> buildings = new ArrayList<BuildingResponse>();
        buildings.add(b0);
        buildings.add(b1);
        buildings.add(b2);

        buildings = BuildingLogic.sortByName(buildings, 1);
        assertEquals(buildings.get(0).getName(), "Aula");
        assertEquals(buildings.get(1).getName(), "EEMCS");
        assertEquals(buildings.get(2).getName(), "Pulse");

        buildings = BuildingLogic.sortByName(buildings, 2);
        assertEquals(buildings.get(2).getName(), "Aula");
        assertEquals(buildings.get(1).getName(), "EEMCS");
        assertEquals(buildings.get(0).getName(), "Pulse");
    }

    @Test
    public void sortByIdTest() {
        List<BuildingResponse> buildings = new ArrayList<BuildingResponse>();
        buildings.add(b0);
        buildings.add(b1);
        buildings.add(b2);

        buildings = BuildingLogic.sortById(buildings, 1);
        assertEquals(buildings.get(0).getName(), "Aula");
        assertEquals(buildings.get(1).getName(), "Pulse");
        assertEquals(buildings.get(2).getName(), "EEMCS");

        buildings = BuildingLogic.sortById(buildings, 2);
        assertEquals(buildings.get(2).getName(), "Aula");
        assertEquals(buildings.get(1).getName(), "Pulse");
        assertEquals(buildings.get(0).getName(), "EEMCS");
    }

    @Test
    public void checkEmptyFieldTest() {
        String[] msg = new String[5];
        msg[0] = "0000";
        msg[1] = "field is empty. not saved";
        msg[2] = "field is empty. not saved";
        msg[3] = "field is empty. not saved";
        msg[4] = "false";

        String[] result;

        String idInString = "";
        String name = "";
        String address = "";
        String faculty = "";
        result = BuildingLogic.checkEmptyField(idInString, name, address, faculty);
        assertEquals(result[0], msg[0]);
        assertEquals(result[1], msg[1]);
        assertEquals(result[2], msg[2]);
        assertEquals(result[3], msg[3]);
        assertEquals(result[4], msg[4]);

        idInString = "33";
        name = "EEMCS";
        address = "Drebbelweg";
        faculty = "EWI";

        msg[0] = "";
        msg[1] = "";
        msg[2] = "";
        msg[3] = "";
        msg[4] = "";

        result = BuildingLogic.checkEmptyField(idInString, name, address, faculty);
        assertEquals(result[0], msg[0]);
        assertEquals(result[1], msg[1]);
        assertEquals(result[2], msg[2]);
        assertEquals(result[3], msg[3]);
        assertEquals(result[4], msg[4]);

        idInString = "33";
        name = "EEMCS";
        address = "field is empty. not saved";
        faculty = "EWI";

        msg[0] = idInString;
        msg[1] = name;
        msg[2] = "field is empty. not saved";
        msg[3] = faculty;
        msg[4] = "false";

        result = BuildingLogic.checkEmptyField(idInString, name, address, faculty);
        assertEquals(result[0], msg[0]);
        assertEquals(result[1], msg[1]);
        assertEquals(result[2], msg[2]);
        assertEquals(result[3], msg[3]);
        assertEquals(result[4], msg[4]);

    }

    @Test
    public void getBuildingIdToDeleteTest() {
        String building1 = "Building 33: EEMCS";
        String building2 = "Building 13: Pulse";

        int id1 = BuildingLogic.getBuildingIdToDelete(building1);
        int id2 = BuildingLogic.getBuildingIdToDelete(building2);

        assertEquals(id1, 33);
        assertEquals(id2, 13);

    }
}
