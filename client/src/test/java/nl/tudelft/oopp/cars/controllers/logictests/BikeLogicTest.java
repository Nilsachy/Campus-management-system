package nl.tudelft.oopp.cars.controllers.logictests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.tudelft.oopp.cars.logic.BikeLogic;
import nl.tudelft.oopp.shared.responses.content.AvailableBikeStoragesResponse;
import nl.tudelft.oopp.shared.responses.content.BikeStorageResponse;
import nl.tudelft.oopp.shared.responses.content.FacultiesResponse;
import nl.tudelft.oopp.shared.responses.content.FacultyResponse;
import org.junit.jupiter.api.Test;

public class BikeLogicTest {

    @Test
    public void parseResponseTest() {
        List<BikeStorageResponse> bikeStorages = new ArrayList<>();
        bikeStorages.add(new BikeStorageResponse("faculty1", 10));
        bikeStorages.add(new BikeStorageResponse("faculty2", 10));
        bikeStorages.add(null);
        bikeStorages.add(new BikeStorageResponse("faculty3", 10));
        bikeStorages.add(new BikeStorageResponse("faculty4", 10));
        bikeStorages.add(new BikeStorageResponse("faculty5", 10));

        AvailableBikeStoragesResponse availableBikeStorages =
                new AvailableBikeStoragesResponse();
        availableBikeStorages.setBikeStorages(bikeStorages);
        ObservableList<String> list = FXCollections.observableArrayList();

        BikeLogic.parseResponse(list, availableBikeStorages);

        assertEquals(list.size(), 5);
        assertEquals(list.get(4), "Faculty: faculty5");
        assertEquals(list.get(0), "Faculty: faculty1");
    }

    @Test
    public void parseAdminResponseTest() {
        List<BikeStorageResponse> bikeStorages = new ArrayList<>();
        bikeStorages.add(new BikeStorageResponse("faculty1", 10));
        bikeStorages.add(new BikeStorageResponse("faculty2", 10));
        bikeStorages.add(null);
        bikeStorages.add(new BikeStorageResponse("faculty3", 10));
        bikeStorages.add(new BikeStorageResponse("faculty4", 10));
        bikeStorages.add(new BikeStorageResponse("faculty5", 10));

        AvailableBikeStoragesResponse availableBikeStorages =
                new AvailableBikeStoragesResponse();
        availableBikeStorages.setBikeStorages(bikeStorages);
        ObservableList<String> list = FXCollections.observableArrayList();

        BikeLogic.parseAdminResponse(list, availableBikeStorages);

        assertEquals(list.size(), 5);
        assertEquals(list.get(4), "BikeStorage located at faculty5 with a capacity of 10");
        assertEquals(list.get(0), "BikeStorage located at faculty1 with a capacity of 10");
    }

    @Test
    public void sortingBikeOptionTest() {
        BikeStorageResponse b0 = new BikeStorageResponse("faculty4", 10);
        BikeStorageResponse b1 = new BikeStorageResponse("faculty2", 30);
        BikeStorageResponse b2 = new BikeStorageResponse("faculty1", 25);
        BikeStorageResponse b3 = new BikeStorageResponse("faculty5", 50);
        BikeStorageResponse b4 = new BikeStorageResponse("faculty3", 5);

        List<BikeStorageResponse> bikeStorages = new ArrayList<>();
        bikeStorages.add(b0);
        bikeStorages.add(b1);
        bikeStorages.add(b2);
        bikeStorages.add(b3);
        bikeStorages.add(b4);

        AvailableBikeStoragesResponse availableBikeStorages = new AvailableBikeStoragesResponse();
        availableBikeStorages.setBikeStorages(bikeStorages);

        List<BikeStorageResponse> resultByFacultyAscending = new ArrayList<>();
        resultByFacultyAscending.add(b2);
        resultByFacultyAscending.add(b1);
        resultByFacultyAscending.add(b4);
        resultByFacultyAscending.add(b0);
        resultByFacultyAscending.add(b3);

        availableBikeStorages = BikeLogic.sortingBikeOption(availableBikeStorages, 1, 2);

        assertEquals(resultByFacultyAscending.get(0),
                availableBikeStorages.getBikeStorages().get(0));
        assertEquals(resultByFacultyAscending.get(1),
                availableBikeStorages.getBikeStorages().get(1));
        assertEquals(resultByFacultyAscending.get(2),
                availableBikeStorages.getBikeStorages().get(2));
        assertEquals(resultByFacultyAscending.get(3),
                availableBikeStorages.getBikeStorages().get(3));
        assertEquals(resultByFacultyAscending.get(4),
                availableBikeStorages.getBikeStorages().get(4));


        List<BikeStorageResponse> resultByFacultyDescending = new ArrayList<>();
        resultByFacultyDescending.add(b3);
        resultByFacultyDescending.add(b0);
        resultByFacultyDescending.add(b4);
        resultByFacultyDescending.add(b1);
        resultByFacultyDescending.add(b2);

        availableBikeStorages = BikeLogic.sortingBikeOption(availableBikeStorages, 1, 1);

        assertEquals(availableBikeStorages.getBikeStorages().get(0),
                resultByFacultyDescending.get(0));
        assertEquals(availableBikeStorages.getBikeStorages().get(1),
                resultByFacultyDescending.get(1));
        assertEquals(availableBikeStorages.getBikeStorages().get(2),
                resultByFacultyDescending.get(2));
        assertEquals(availableBikeStorages.getBikeStorages().get(3),
                resultByFacultyDescending.get(3));
        assertEquals(availableBikeStorages.getBikeStorages().get(4),
                resultByFacultyDescending.get(4));


        List<BikeStorageResponse> resultByCapacityAscending = new ArrayList<>();
        resultByCapacityAscending.add(b4);
        resultByCapacityAscending.add(b0);
        resultByCapacityAscending.add(b2);
        resultByCapacityAscending.add(b1);
        resultByCapacityAscending.add(b3);

        availableBikeStorages = BikeLogic.sortingBikeOption(availableBikeStorages, 2, 1);

        assertEquals(availableBikeStorages.getBikeStorages().get(0),
                resultByCapacityAscending.get(0));
        assertEquals(availableBikeStorages.getBikeStorages().get(1),
                resultByCapacityAscending.get(1));
        assertEquals(availableBikeStorages.getBikeStorages().get(2),
                resultByCapacityAscending.get(2));
        assertEquals(availableBikeStorages.getBikeStorages().get(3),
                resultByCapacityAscending.get(3));
        assertEquals(availableBikeStorages.getBikeStorages().get(4),
                resultByCapacityAscending.get(4));


        List<BikeStorageResponse> resultByCapacityDescending = new ArrayList<>();
        resultByCapacityDescending.add(b3);
        resultByCapacityDescending.add(b1);
        resultByCapacityDescending.add(b2);
        resultByCapacityDescending.add(b0);
        resultByCapacityDescending.add(b4);

        availableBikeStorages = BikeLogic.sortingBikeOption(availableBikeStorages, 2, 2);

        assertEquals(availableBikeStorages.getBikeStorages().get(0),
                resultByCapacityDescending.get(0));
        assertEquals(availableBikeStorages.getBikeStorages().get(1),
                resultByCapacityDescending.get(1));
        assertEquals(availableBikeStorages.getBikeStorages().get(2),
                resultByCapacityDescending.get(2));
        assertEquals(availableBikeStorages.getBikeStorages().get(3),
                resultByCapacityDescending.get(3));
        assertEquals(availableBikeStorages.getBikeStorages().get(4),
                resultByCapacityDescending.get(4));

    }

    @Test
    public void sortByFacultyNameTest() {
        BikeStorageResponse b0 = new BikeStorageResponse("faculty4", 10);
        BikeStorageResponse b1 = new BikeStorageResponse("faculty2", 10);
        BikeStorageResponse b2 = new BikeStorageResponse("faculty1", 10);
        BikeStorageResponse b3 = new BikeStorageResponse("faculty5", 10);
        BikeStorageResponse b4 = new BikeStorageResponse("faculty3", 10);

        List<BikeStorageResponse> bikeStorages = new ArrayList<>();
        bikeStorages.add(b0);
        bikeStorages.add(b1);
        bikeStorages.add(b2);
        bikeStorages.add(b3);
        bikeStorages.add(b4);

        List<BikeStorageResponse> resultAscending = new ArrayList<>();
        resultAscending.add(b2);
        resultAscending.add(b1);
        resultAscending.add(b4);
        resultAscending.add(b0);
        resultAscending.add(b3);

        bikeStorages = BikeLogic.sortByFacultyName(bikeStorages, 2);

        assertEquals(bikeStorages.get(0), resultAscending.get(0));
        assertEquals(bikeStorages.get(1), resultAscending.get(1));
        assertEquals(bikeStorages.get(2), resultAscending.get(2));
        assertEquals(bikeStorages.get(3), resultAscending.get(3));
        assertEquals(bikeStorages.get(4), resultAscending.get(4));


        List<BikeStorageResponse> resultDescending = new ArrayList<>();
        resultDescending.add(b3);
        resultDescending.add(b0);
        resultDescending.add(b4);
        resultDescending.add(b1);
        resultDescending.add(b2);

        bikeStorages = BikeLogic.sortByFacultyName(bikeStorages, 1);

        assertEquals(bikeStorages.get(0), resultDescending.get(0));
        assertEquals(bikeStorages.get(1), resultDescending.get(1));
        assertEquals(bikeStorages.get(2), resultDescending.get(2));
        assertEquals(bikeStorages.get(3), resultDescending.get(3));
        assertEquals(bikeStorages.get(4), resultDescending.get(4));

    }

    @Test
    public void sortByCapacityTest() {
        BikeStorageResponse b0 = new BikeStorageResponse("faculty4", 10);
        BikeStorageResponse b1 = new BikeStorageResponse("faculty2", 30);
        BikeStorageResponse b2 = new BikeStorageResponse("faculty1", 25);
        BikeStorageResponse b3 = new BikeStorageResponse("faculty5", 50);
        BikeStorageResponse b4 = new BikeStorageResponse("faculty3", 5);

        List<BikeStorageResponse> bikeStorages = new ArrayList<>();
        bikeStorages.add(b0);
        bikeStorages.add(b1);
        bikeStorages.add(b2);
        bikeStorages.add(b3);
        bikeStorages.add(b4);

        List<BikeStorageResponse> resultAscending = new ArrayList<>();
        resultAscending.add(b4);
        resultAscending.add(b0);
        resultAscending.add(b2);
        resultAscending.add(b1);
        resultAscending.add(b3);

        bikeStorages = BikeLogic.sortByCapacity(bikeStorages, 1);

        assertEquals(bikeStorages.get(0), resultAscending.get(0));
        assertEquals(bikeStorages.get(1), resultAscending.get(1));
        assertEquals(bikeStorages.get(2), resultAscending.get(2));
        assertEquals(bikeStorages.get(3), resultAscending.get(3));
        assertEquals(bikeStorages.get(4), resultAscending.get(4));


        List<BikeStorageResponse> resultDescending = new ArrayList<>();
        resultDescending.add(b3);
        resultDescending.add(b1);
        resultDescending.add(b2);
        resultDescending.add(b0);
        resultDescending.add(b4);

        bikeStorages = BikeLogic.sortByCapacity(bikeStorages, 2);

        assertEquals(bikeStorages.get(0), resultDescending.get(0));
        assertEquals(bikeStorages.get(1), resultDescending.get(1));
        assertEquals(bikeStorages.get(2), resultDescending.get(2));
        assertEquals(bikeStorages.get(3), resultDescending.get(3));
        assertEquals(bikeStorages.get(4), resultDescending.get(4));
    }

    @Test
    public void parseFacultyResponseTest() {
        List<FacultyResponse> listOfFaculties = new ArrayList<>();
        listOfFaculties.add(new FacultyResponse("1", "faculty1", "+31 (0)15 27 898 01"));
        listOfFaculties.add(new FacultyResponse("2", "faculty2", "+31 (0)15 27 898 02"));
        listOfFaculties.add(null);
        listOfFaculties.add(new FacultyResponse("3", "faculty3", "+31 (0)15 27 898 03"));
        listOfFaculties.add(new FacultyResponse("4", "faculty4", "+31 (0)15 27 898 04"));
        listOfFaculties.add(new FacultyResponse("5", "faculty5", "+31 (0)15 27 898 05"));

        FacultiesResponse facultiesResponse = new FacultiesResponse();
        facultiesResponse.setFaculties(listOfFaculties);
        ObservableList<String> list = FXCollections.observableArrayList();

        BikeLogic.parseFacultyResponse(list, facultiesResponse);

        assertEquals(list.size(), 5);
        assertEquals(list.get(4), "5");
        assertEquals(list.get(0), "1");
    }


    @Test
    public void getStorageIdToDeleteTest() {
        String bikeStorage1 = "BikeStorage located at faculty4 with a capacity of 10";
        String bikeStorage2 = "BikeStorage located at faculty1 with a capacity of 25";

        String id1 = BikeLogic.getStorageIdToDelete(bikeStorage1);
        String id2 = BikeLogic.getStorageIdToDelete(bikeStorage2);

        assertEquals(id1, "faculty4");
        assertEquals(id2, "faculty1");
    }
}
