package nl.tudelft.oopp.cars.controllers.general;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import nl.tudelft.oopp.cars.communication.CurrentPage;
import nl.tudelft.oopp.cars.communication.ServerReservationCommunication;
import nl.tudelft.oopp.cars.communication.ServerUserCommunication;
import nl.tudelft.oopp.cars.controllers.MainController;
import nl.tudelft.oopp.cars.logic.ErrorMessageLogic;
import nl.tudelft.oopp.cars.logic.ReservationLogic;
import nl.tudelft.oopp.shared.responses.content.BikeReservationResponse;
import nl.tudelft.oopp.shared.responses.content.CustomEventResponse;
import nl.tudelft.oopp.shared.responses.content.CustomEventsResponse;
import nl.tudelft.oopp.shared.responses.content.ReservationResponse;
import nl.tudelft.oopp.shared.responses.content.RoomReservationResponse;

public class AccountInterfaceController {

    @FXML private HBox accountPage;
    @FXML private TextField email;
    @FXML private PasswordField password;
    @FXML private ChoiceBox<String> ordering;
    @FXML private ListView<String> listView;
    @FXML private Label settingsErrorMessage;
    @FXML private Label eventsErrorMessage;

    @FXML MainController main;

    List<ReservationResponse> reservations;
    private List<ReservationResponse> all = new ArrayList<>();

    private ObservableList<String> list = FXCollections.observableArrayList();

    /**
     * Called to establish a connection with the mainController.
     * @param mainController the mainController calling this method
     */
    public void init(MainController mainController) {
        System.out.println("AccountScreenController connected to the MainController.");
        main = mainController;
        CurrentPage.cls = this.getClass();
        CurrentPage.page = this.accountPage;
        CurrentPage.onePage = true;
    }

    /**
     * Automatically called when the page is loaded.
     */
    @FXML public void initialize() throws IOException {
        String currentUser = ServerUserCommunication.getThisUser();

        reservations = ServerReservationCommunication.getReservations(currentUser, "previous");
        CustomEventsResponse events = ServerReservationCommunication
                .getCustomEvents(currentUser, "previous");
        all.addAll(reservations);
        all.addAll(events.getEvents());

        email.setText(currentUser);

        ReservationLogic.initializeEvents(all, ordering, list, listView);

        addListener(ordering);
    }

    /**
     * Adds a listener to the given choiceBox.
     * @param ordering the choiceBox that should be listened to for changes
     */
    public void addListener(ChoiceBox<String> ordering) {
        ordering.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (!(oldValue.equals(newValue))) {
                        ReservationLogic.initializeEvents(all, ordering,
                                    list, listView);
                    }
                });
    }

    /**
     * Called when the Change Password button is clicked.
     */
    public void onChangePasswordClicked() throws IOException {
        String email = this.email.getText();
        String password = this.password.getText();

        settingsErrorMessage.setText(ErrorMessageLogic.accountSettingsMessage(password));

        String verify = ServerUserCommunication.changePassword(email, password.hashCode());

        this.password.setText("");
        settingsErrorMessage.setText(verify);
    }

    /**
     * Called when the Log Out button is clicked.
     * @throws IOException exception thrown when the loginInterface.fxml file fails to load
     */
    public void onSignOutClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/general/loginInterface.fxml"));
        Parent root = loader.load();

        Stage main = new Stage();
        main.setTitle("Login");
        main.setScene(new Scene(root));
        main.show();

        Stage stage = (Stage) accountPage.getScene().getWindow();
        stage.close();
    }

    /**
     * Called when the Plus button is clicked.
     */
    public void onAddEventClicked() throws IOException {
        main.loadView("/general/customEventInterface.fxml");
    }

    /**
     * Called when the Rebook Event button is clicked.
     */
    public void onRebookEventClicked() throws IOException, ParseException {
        String event = listView.getSelectionModel().getSelectedItem();

        String errorMessage = ErrorMessageLogic.accountEventsMessage(event);
        eventsErrorMessage.setText(errorMessage);

        if (errorMessage.equals("")) {
            if (event.startsWith("Room Reservation")) {
                RoomReservationResponse room = new RoomReservationResponse();
                //ReservationLogic.parseReservation(event, room);
                //main.setRoomReservation(room);
                main.loadView("/reservations/BookARoomInterface.fxml");
                //TODO: Implement this
            } else if (event.startsWith("Bike Rental")) {
                BikeReservationResponse bike = new BikeReservationResponse();
                //ReservationLogic.parseReservation(event, bike);
                //main.setBikeReservation(bike);
                main.loadView("/reservations/rentABikeInterface.fxml");
                //TODO: Implement this
            } else {
                CustomEventResponse custom = new CustomEventResponse();
                //ReservationLogic.parseReservation(event, custom);
                //TODO: Implement this
            }
        }
    }
}
