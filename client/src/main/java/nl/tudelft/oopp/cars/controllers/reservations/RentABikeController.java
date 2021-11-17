package nl.tudelft.oopp.cars.controllers.reservations;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import nl.tudelft.oopp.cars.communication.CurrentPage;
import nl.tudelft.oopp.cars.communication.ServerEntityCommunication;
import nl.tudelft.oopp.cars.communication.ServerReservationCommunication;
import nl.tudelft.oopp.cars.controllers.MainController;
import nl.tudelft.oopp.cars.logic.BikeLogic;
import nl.tudelft.oopp.cars.logic.ErrorMessageLogic;
import nl.tudelft.oopp.cars.logic.GeneralLogic;
import nl.tudelft.oopp.shared.responses.content.AvailableBikeStoragesResponse;

public class RentABikeController {

    @FXML private HBox bikePage;

    @FXML private Label errorMessage;
    @FXML private DatePicker fromDate;
    @FXML private DatePicker toDate;
    @FXML private TextField fromTime;
    @FXML private TextField toTime;
    @FXML private ListView<String> departListView;
    @FXML private ListView<String> returnListView;

    @FXML MainController main;

    Calendar fromCalendar;
    Calendar toCalendar;

    AvailableBikeStoragesResponse returnBikeStorages;

    ObservableList<String> departList = FXCollections.observableArrayList();
    ObservableList<String> returnList = FXCollections.observableArrayList();

    /**
     * Called to establish a connection with the mainController.
     * @param mainController the mainController calling this method
     */
    public void init(MainController mainController) {
        System.out.println("RentABikeController connected to the MainController.");
        main = mainController;

        CurrentPage.cls = this.getClass();
        CurrentPage.page = this.bikePage;
        CurrentPage.onePage = true;
    }

    /**
     * Automatically called when the page is loaded.
     */
    @FXML public void initialize() throws IOException {
        returnBikeStorages = ServerEntityCommunication.getAllBikeStorages();

        initializeReturnListView();

        addListener(fromTime);
        addListener(toTime);
    }

    /**
     * Makes sure that the things typed into the textField are numbers.
     * @param text the textfield to listen to
     */
    public void addListener(TextField text) {
        text.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() == 5) {
                Platform.runLater(() -> {
                    text.getStyleClass().add("blackText2");
                });
            } else {
                Platform.runLater(() -> {
                    text.getStyleClass().add("negativeMessage");
                });
            }
            if (oldValue.length() == 5 && oldValue.length() <= newValue.length()) {
                Platform.runLater(() -> {
                    text.setText(oldValue);
                });
            } else if (newValue.length() < oldValue.length()) {
                Platform.runLater(() -> {
                    text.setText("");
                });
            }

            if (newValue.length() == 2) {
                text.setText(newValue + ":");
            }

            if (!newValue.matches("[0-9, :]*")) {
                Platform.runLater(() -> {
                    text.setText(oldValue);
                });
            }
        });
    }

    /**
     * Populates the departListView with the corresponding bike storages.
     */
    public void initializeDepartListView() throws IOException {
        departList.clear();
        AvailableBikeStoragesResponse availableBikeStorages = ServerReservationCommunication
                .getAvailableBikeStorages(fromCalendar, toCalendar);
        if (availableBikeStorages != null && availableBikeStorages.getBikeStorages() != null) {
            BikeLogic.parseResponse(departList, availableBikeStorages);
        }
        departListView.setItems(departList);
    }

    /**
     * Populates the departListView with all bike storages.
     */
    public void initializeReturnListView() {
        returnList.clear();
        if (returnBikeStorages != null && returnBikeStorages.getBikeStorages() != null) {
            BikeLogic.parseResponse(returnList, returnBikeStorages);
        }
        returnListView.setItems(returnList);
    }

    /**
     * Checks if all fields have been filled out.
     * @param fromDay day from which to rent the bike.
     * @param toDay day until which to rent the bike.
     */
    public void errorCheck(String fromDay, String toDay) {
        String fromTime = this.fromTime.getText();
        String toTime = this.toTime.getText();

        errorMessage.setText(ErrorMessageLogic.bikeFromToMessage(fromDay, toDay, fromTime, toTime));
    }

    /**
     * Called when you click on the find a depart building button.
     * @throws ParseException called when it fails to parse the dates
     * @throws IOException called when it fails to load the listView
     */
    public void onFindADepartBuildingClicked() throws ParseException, IOException {
        errorMessage.getStyleClass().add("negativeMessage");
        errorMessage.setText("");

        String fromTime = this.fromTime.getText();
        String toTime = this.toTime.getText();
        String fromDay = this.fromDate.getEditor().getText();
        String toDay = this.toDate.getEditor().getText();
        errorCheck(fromDay, toDay);

        if (GeneralLogic.isValidDate(fromDay) && GeneralLogic.isValidDate(toDay)) {
            errorMessage.setText("");
            fromCalendar = GeneralLogic.parseDateTime("", fromDay + " " + fromTime);
            toCalendar = GeneralLogic.parseDateTime("", toDay + " " + toTime);

            Calendar c = Calendar.getInstance();
            if (toCalendar.after(c) && fromCalendar.after(c)
                    && toCalendar.after(fromCalendar)) {
                initializeDepartListView();
            } else {
                errorMessage.setText("Please select a proper date and time");
            }
        }
    }

    /**
     * Called when you click on the rent a bike button.
     */
    public void onRentTheBikeClicked() throws IOException, ParseException {
        String departBikeStorage = departListView.getSelectionModel().getSelectedItem();
        String returnBikeStorage = returnListView.getSelectionModel().getSelectedItem();
        departBikeStorage = BikeLogic.getStorageName(departBikeStorage);
        returnBikeStorage = BikeLogic.getStorageName(returnBikeStorage);
        errorMessage.setText(ErrorMessageLogic.bikeRentMessage(departBikeStorage,
                                                                    returnBikeStorage));
        if (errorMessage.getText().equals("")) {

            String response = ServerReservationCommunication.rentBike(main.getCurrentUser(),
                    departBikeStorage, returnBikeStorage, fromCalendar, toCalendar);

            System.out.println(response);
            onFindADepartBuildingClicked();
            errorMessage.getStyleClass().clear();
            errorMessage.getStyleClass().add("positiveMessage");
            errorMessage.setText(response);
        }
    }
}
