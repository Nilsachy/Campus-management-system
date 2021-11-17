package nl.tudelft.oopp.cars.controllers.admin;

import java.io.IOException;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import nl.tudelft.oopp.cars.communication.CurrentPage;
import nl.tudelft.oopp.cars.communication.ServerEntityCommunication;
import nl.tudelft.oopp.cars.controllers.MainController;
import nl.tudelft.oopp.cars.logic.BikeLogic;
import nl.tudelft.oopp.cars.logic.GeneralLogic;
import nl.tudelft.oopp.shared.responses.content.AvailableBikeStoragesResponse;
import nl.tudelft.oopp.shared.responses.content.FacultiesResponse;

public class AdminBikeStorageInterfaceController {

    @FXML private Button removeStorageButton;
    @FXML private Button addStorageButton;
    @FXML private HBox adminBikeStorage;

    @FXML private ListView<String> facultyListView;
    @FXML private ListView<String> bikeStorageListView;
    @FXML private ChoiceBox<String> ordering;
    @FXML private TextField maxAvailable;

    @FXML private Label feedbackAddMessage;
    @FXML private Label feedbackRemoveMessage;

    @FXML MainController main;

    AvailableBikeStoragesResponse bikeStorages;
    FacultiesResponse facultiesResponse;

    private ObservableList<String> list = FXCollections.observableArrayList();
    private ObservableList<String> list1 = FXCollections.observableArrayList();

    /**
     * Called to establish a connection with the mainController.
     * @param mainController the mainController calling this method
     */
    public void init(MainController mainController) {
        System.out.println("AdminBikeStorageController connected to the MainController.");
        main = mainController;
        CurrentPage.cls = this.getClass();
        CurrentPage.page = this.adminBikeStorage;
        CurrentPage.onePage = true;
    }

    /**
     * Automatically called when the page is loaded.
     */
    @FXML public void initialize() throws IOException {
        bikeStorages = ServerEntityCommunication.getAllBikeStorages();
        facultiesResponse = ServerEntityCommunication.getAllFaculties();

        BikeLogic.parseFacultyResponse(list1, facultiesResponse);
        facultyListView.setItems(list1);

        BikeLogic.sortingBikeOption(bikeStorages, 2, 1);
        BikeLogic.parseAdminResponse(list, bikeStorages);
        bikeStorageListView.setItems(list);

        ordering.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    list.clear();
                    if (!(oldValue.equals(newValue))) {
                        int[] order = GeneralLogic.parseOrder(ordering.getSelectionModel()
                                .getSelectedItem());
                        BikeLogic.sortingBikeOption(bikeStorages, order[0], order[1]);
                        BikeLogic.parseAdminResponse(list, bikeStorages);
                    }
                });

        maxAvailable.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9]*")) {
                maxAvailable.setText(oldValue);
            }
        });
    }

    /**
     * Called when the Add Bike Storage button is clicked, adds a building to the database.
     */
    public void onAddBikeStorageClicked() throws IOException {
        facultyListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    addStorageButton.setTextFill(Color.BLACK);
                    addStorageButton.setText("Add Bike Storage");
                });
        this.maxAvailable.textProperty().addListener((observable, oldValue, newValue) -> {
            addStorageButton.setTextFill(Color.BLACK);
            addStorageButton.setText("Add Bike Storage");
        });
        String facultyToAdd = facultyListView.getSelectionModel().getSelectedItem();
        String capacityInStr = this.maxAvailable.getText();
        int capacity = 0;
        if (!(capacityInStr == null || capacityInStr.equals(""))) {
            capacity = Integer.parseInt(capacityInStr);
        }
        ServerEntityCommunication.addBikeStorage(facultyToAdd, capacity);
        bikeStorages
                = ServerEntityCommunication.getAllBikeStorages();
        list.clear();
        BikeLogic.sortingBikeOption(bikeStorages, 2, 1);
        BikeLogic.parseAdminResponse(list, bikeStorages);
        bikeStorageListView.setItems(list);

        feedbackAddMessage.getStyleClass().clear();
        feedbackAddMessage.getStyleClass().add("positiveMessage");
        feedbackAddMessage.setText("saved");
    }

    /**
     * Called when the Remove Building button is clicked, removes a building from the database.
     */
    public void onRemoveBikeStorageClicked() throws IOException {
        String storageToDelete = bikeStorageListView.getSelectionModel().getSelectedItem();
        bikeStorageListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (oldValue.equals(storageToDelete)) {
                        Platform.runLater(() -> {
                            feedbackRemoveMessage.getStyleClass().clear();
                            feedbackRemoveMessage.getStyleClass().add("positiveMessage");
                            feedbackRemoveMessage.setText("remove successfully");
                        });
                    }
                });

        if (storageToDelete != null && storageToDelete.length() > 0) {
            String idToDelete = BikeLogic.getStorageIdToDelete(storageToDelete);
            ServerEntityCommunication.removeBikeStorage(idToDelete);
            bikeStorageListView.getItems().remove(storageToDelete);
        }

    }
}
