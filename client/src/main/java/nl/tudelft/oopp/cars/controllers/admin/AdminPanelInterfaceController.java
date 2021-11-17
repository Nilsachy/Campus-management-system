package nl.tudelft.oopp.cars.controllers.admin;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import nl.tudelft.oopp.cars.communication.CurrentPage;
import nl.tudelft.oopp.cars.communication.ServerEntityCommunication;
import nl.tudelft.oopp.cars.controllers.MainController;
import nl.tudelft.oopp.cars.logic.AnnouncementLogic;
import nl.tudelft.oopp.cars.logic.GeneralLogic;
import nl.tudelft.oopp.shared.responses.content.AnnouncementsResponse;

public class AdminPanelInterfaceController {

    @FXML private HBox adminPanel;

    @FXML private ListView<String> listView;
    @FXML private ChoiceBox<String> ordering;

    @FXML MainController main;

    private ObservableList<String> list = FXCollections.observableArrayList();

    /**
     * Called to establish a connection with the mainController.
     * @param mainController the mainController calling this method
     */
    public void init(MainController mainController) {
        System.out.println("AdminPanelController connected to the MainController.");
        main = mainController;
        CurrentPage.cls = this.getClass();
        CurrentPage.page = this.adminPanel;
        CurrentPage.onePage = true;
    }

    /**
     * Automatically called when the page is loaded.
     */
    @FXML public void initialize() throws IOException {
        AnnouncementsResponse announcements = ServerEntityCommunication.getAllAnnouncements();

        GeneralLogic.wrapText(listView);

        AnnouncementLogic.initializeAnnouncements("Admin", announcements, ordering, list, listView);
    }

    /**
     * Called when the Manipulate Rooms button is clicked, loads the admin room page.
     * @throws IOException called when it fails to load the admin room interface fxml
     */
    public void onManipulateRoomsClicked() throws IOException {
        main.loadView("/admin/adminRoomInterface.fxml");
    }

    /**
     * Called when the Manipulate Buildings button is clicked, loads the admin building page.
     * @throws IOException called when it fails to load the admin building interface fxml
     */
    public void onManipulateBuildingsClicked() throws IOException {
        main.loadView("/admin/adminBuildingInterface.fxml");
    }

    /**
     * Called when the Manipulate Bike Storages button is clicked, loads the admin bikestorage page.
     * @throws IOException called when it fails to load the admin bikestorage interface fxml
     */
    public void onManipulateBikeStoragesClicked() throws IOException {
        main.loadView("/admin/adminBikeStorageInterface.fxml"
        );
    }

    /**
     * Called when the Manipulate Bike Storages button is clicked, loads the admin bikestorage page.
     * @throws IOException called when it fails to load the admin bikestorage interface fxml
     */
    public void onManipulateAnnouncementsClicked() throws IOException {
        main.loadView("/admin/adminAnnouncementsInterface.fxml"
        );
    }
}
