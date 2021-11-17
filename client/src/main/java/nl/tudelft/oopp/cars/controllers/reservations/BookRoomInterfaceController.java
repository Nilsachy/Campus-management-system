package nl.tudelft.oopp.cars.controllers.reservations;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import nl.tudelft.oopp.cars.communication.CurrentPage;
import nl.tudelft.oopp.cars.communication.ServerEntityCommunication;
import nl.tudelft.oopp.cars.controllers.MainController;
import nl.tudelft.oopp.cars.logic.BuildingLogic;
import nl.tudelft.oopp.cars.logic.GeneralLogic;
import nl.tudelft.oopp.shared.responses.content.BuildingsResponse;

public class BookRoomInterfaceController {

    @FXML private HBox buildingPage;

    @FXML private ChoiceBox<String> ordering;
    @FXML private ListView<String> buildingsListView;
    @FXML private Label buildingName;
    @FXML private Label buildingInfo;
    @FXML private Label errorMessage;

    @FXML MainController main;

    private BuildingsResponse buildings;

    private ObservableList<String> list = FXCollections.observableArrayList();

    /**
     * Called to establish a connection with the mainController.
     * @param mainController the mainController calling this method
     */
    public void init(MainController mainController) {
        System.out.println("BookRoomController connected to the MainController.");
        main = mainController;
        CurrentPage.cls = this.getClass();
        CurrentPage.page = this.buildingPage;
        CurrentPage.onePage = true;
    }

    /**
     * Automatically called when the page is loaded.
     */
    @FXML public void initialize() throws IOException {
        buildings = ServerEntityCommunication.getAllBuildings();
        initializeBuildings();

        ordering.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (!(oldValue.equals(newValue))) {
                        initializeBuildings();
                    }
                });
    }

    /**
     * Fills the listView with the buildings from the BuildingsResponse.
     */
    public void initializeBuildings() {
        list.clear();
        int[] order = GeneralLogic.parseOrder(this.ordering
                .getSelectionModel().getSelectedItem());
        if (buildings != null && buildings.getBuildings() != null) {
            buildings = BuildingLogic.sortingBuildingOption(buildings,
                    order[0], order[1]);
            BuildingLogic.parseResponse(list, buildings);
        }
        buildingsListView.setItems(list);
    }

    /**
     * Called when you click on a building in the list.
     */
    public void onListViewClicked() throws IOException {
        errorMessage.setText("");

        int id = BuildingLogic.parseStringId(buildingsListView
                .getSelectionModel().getSelectedItem());
        String building = ServerEntityCommunication.getBuildingById(id);

        buildingName.setText(buildingsListView.getSelectionModel().getSelectedItem());
        buildingInfo.setText(building);
        System.out.println(buildingsListView.getSelectionModel().getSelectedItem());
    }

    /**
     * Called when you click on the button to rent a room in a given building.
     * @throws IOException called when it fails to load the book a room interface fxml
     */
    public void onBookARoomInBuildingClicked() throws IOException {
        String building = buildingsListView.getSelectionModel().getSelectedItem();
        if (building == null) {
            errorMessage.setText("Please select a building");
        } else {
            main.setBuilding(building);
            main.loadView("/reservations/bookARoomInterface.fxml");
        }
    }
}
