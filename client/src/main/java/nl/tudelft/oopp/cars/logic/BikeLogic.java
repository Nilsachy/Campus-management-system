package nl.tudelft.oopp.cars.logic;

import java.util.Comparator;
import java.util.List;
import javafx.collections.ObservableList;
import nl.tudelft.oopp.shared.responses.content.AvailableBikeStoragesResponse;
import nl.tudelft.oopp.shared.responses.content.BikeStorageResponse;
import nl.tudelft.oopp.shared.responses.content.FacultiesResponse;
import nl.tudelft.oopp.shared.responses.content.FacultyResponse;
import nl.tudelft.oopp.shared.responses.content.RoomResponse;

public class BikeLogic {

    /**
     * Puts the AvailableBikeStoragesResponse in an observable list.
     * @param availableBikeStorages response containing all available bike storages
     */
    public static void parseResponse(ObservableList<String> list,
                                     AvailableBikeStoragesResponse availableBikeStorages) {
        for (BikeStorageResponse bikeStorage : availableBikeStorages.getBikeStorages()) {
            if (bikeStorage == null) {
                continue;
            }
            String bikeStorageString = String.format("Faculty: %s", bikeStorage.getFaculty());
            list.add(bikeStorageString);
        }
    }

    /**
     * Puts the AvailableBikeStoragesAdminResponse in an observable list.
     * @param availableBikeStorages response containing all available bike storages
     */
    public static void parseAdminResponse(ObservableList<String> list,
                                    AvailableBikeStoragesResponse availableBikeStorages) {
        for (BikeStorageResponse bikeStorage : availableBikeStorages.getBikeStorages()) {
            if (bikeStorage == null) {
                continue;
            }
            String bikeStorageString = String.format("BikeStorage located at %s with a capacity "
                    + "of %d", bikeStorage.getFaculty(), bikeStorage.getCapacity());
            list.add(bikeStorageString);
        }
    }

    /**
     * Logic for sorting bikeStorages.
     * @param bikeStorages list of bikeStorages
     * @param std 1: sort by name, 2: sort by capacity
     * @param opt 1: ascending 2: descending
     * @return sorted list of all bikeStorages that are displayed
     */
    public static AvailableBikeStoragesResponse sortingBikeOption(
            AvailableBikeStoragesResponse bikeStorages, int std, int opt) {
        List<BikeStorageResponse> storages = bikeStorages.getBikeStorages();
        if (std == 1) {
            storages = BikeLogic.sortByFacultyName(storages, opt);
        } else {
            storages = BikeLogic.sortByCapacity(storages, opt);
        }

        return new AvailableBikeStoragesResponse(storages);
    }

    /**
     * Logic for sorting storage depending on their faculty.
     * @param storages list of storages
     * @param option 1: ascending 2: descending
     * @return sorted list of all storages that are available
     */
    public static List<BikeStorageResponse> sortByFacultyName(
            List<BikeStorageResponse> storages, int option) {
        int i = -1;
        if (option == 2) {
            i = 1;
        }
        int finalI = i;
        storages.sort((b1, b2) -> {
            if (b1.getFaculty().compareTo(b2.getFaculty()) < 0) {
                return -1 * finalI;
            } else if (b1.getFaculty().compareTo(b2.getFaculty()) > 0) {
                return finalI;
            }
            return 0;
        });
        return storages;
    }

    /**
     * Logic for sorting storages depending on their capacity.
     * @param storages list of storages
     * @param option 1: ascending 2: descending
     * @return sorted list of all rooms that are available
     */
    public static List<BikeStorageResponse> sortByCapacity(
            List<BikeStorageResponse> storages, int option) {
        int i = 1;
        if (option == 2) {
            i = -1;
        }
        int finalI = i;
        storages.sort(new Comparator<BikeStorageResponse>() {
            @Override
            public int compare(BikeStorageResponse b1, BikeStorageResponse b2) {
                if (b1.getCapacity() < b2.getCapacity()) {
                    return -1 * finalI;
                } else if (b1.getCapacity() > b2.getCapacity()) {
                    return finalI;
                }
                return 0;
            }
        });
        return storages;
    }

    /**
     * Puts the AvailableBikeStoragesAdminResponse in an observable list.
     * @param faculties response containing all available faculty
     */
    public static void parseFacultyResponse(ObservableList<String> list,
                                            FacultiesResponse faculties) {
        for (FacultyResponse faculty : faculties.getFaculties()) {
            if (faculty == null) {
                continue;
            }
            String facultyStr = String.format(faculty.getId());
            list.add(facultyStr);
        }
    }

    /**
     * Logic for getting storage id to be deleted.
     * @param storageToDelete room to be deleted
     * @return list of set text for the fields
     */
    public static String getStorageIdToDelete(String storageToDelete) {
        String[] split1 = storageToDelete.split("at ");
        String[] split2 = split1[1].split(" with");

        return split2[0];
    }

    /**
     * Logic for getting storage id to be deleted.
     * @param storage room to be deleted
     * @return list of set text for the fields
     */
    public static String getStorageName(String storage) {
        String[] split1 = storage.split(": ");
        return split1[1];
    }
}
