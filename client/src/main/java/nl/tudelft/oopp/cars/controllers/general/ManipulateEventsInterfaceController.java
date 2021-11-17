package nl.tudelft.oopp.cars.controllers.general;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import nl.tudelft.oopp.cars.communication.CurrentPage;
import nl.tudelft.oopp.cars.communication.ServerReservationCommunication;
import nl.tudelft.oopp.cars.communication.ServerUserCommunication;
import nl.tudelft.oopp.cars.controllers.MainController;
import nl.tudelft.oopp.cars.logic.AnnouncementLogic;
import nl.tudelft.oopp.cars.logic.ErrorMessageLogic;
import nl.tudelft.oopp.cars.logic.GeneralLogic;
import nl.tudelft.oopp.cars.logic.ReservationLogic;
import nl.tudelft.oopp.shared.requests.delete.DeleteBikeReservationRequest;
import nl.tudelft.oopp.shared.requests.delete.DeleteRoomReservationRequest;
import nl.tudelft.oopp.shared.requests.delete.RemoveEventByIdRequest;
import nl.tudelft.oopp.shared.responses.content.CustomEventsResponse;
import nl.tudelft.oopp.shared.responses.content.ReservationResponse;

public class ManipulateEventsInterfaceController {

    @FXML private HBox manipulateEventsScreen;

    @FXML private ListView<String> prevListView;
    @FXML private ListView<String> upListView;
    @FXML private ChoiceBox<String> prevOrdering;
    @FXML private ChoiceBox<String> upOrdering;
    @FXML private Label prevErrorMessage;
    @FXML private Label upErrorMessage;

    @FXML MainController main;

    private List<ReservationResponse> prevAll = new ArrayList<>();
    private List<ReservationResponse> upAll = new ArrayList<>();

    private ObservableList<String> prevList = FXCollections.observableArrayList();
    private ObservableList<String> upList = FXCollections.observableArrayList();

    /**
     * Called to establish a connection with the mainController.
     * @param mainController the mainController calling this method
     */
    public void init(MainController mainController) {
        System.out.println("ManipulateEventsController connected to the MainController.");
        main = mainController;
        CurrentPage.cls = this.getClass();
        CurrentPage.page = this.manipulateEventsScreen;
        CurrentPage.onePage = true;
    }

    /**
     * Automatically called when the page is loaded.
     */
    @FXML public void initialize() throws IOException {
        String currentUser = ServerUserCommunication.getThisUser();

        List<ReservationResponse> prevReservations
                = ServerReservationCommunication.getReservations(currentUser, "previous");
        prevAll.addAll(prevReservations);
        List<ReservationResponse> upReservations
                = ServerReservationCommunication.getReservations(currentUser, "active");
        upAll.addAll(upReservations);
        CustomEventsResponse prevEvents
                = ServerReservationCommunication.getCustomEvents(currentUser, "previous");
        prevAll.addAll(prevEvents.getEvents());
        CustomEventsResponse upEvents
                = ServerReservationCommunication.getCustomEvents(currentUser, "active");
        upAll.addAll(upEvents.getEvents());

        ReservationLogic.initializeEvents(prevAll, prevOrdering, prevList, prevListView);
        ReservationLogic.initializeEvents(upAll, upOrdering, upList, upListView);

        addListener(prevAll, prevOrdering, prevList, prevListView);
        addListener(upAll, upOrdering, upList, upListView);
    }

    /**
     * Adds a listener to the given choiceBox.
     * @param ordering the choiceBox that should be listened to for changes
     */
    public void addListener(List<ReservationResponse> reservations, ChoiceBox<String> ordering,
                            ObservableList<String> list, ListView<String> listView) {
        ordering.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (!(oldValue.equals(newValue))) {
                        ReservationLogic.initializeEvents(reservations, ordering, list, listView);
                    }
                });
    }

    /**
     * Called when the Remove Previous Event button is clicked.
     */
    public void onRemovePrevEventClicked() throws IOException, ParseException {
        removeEvent(prevAll, prevListView);
    }

    /**
     * Called when the Remove Upcoming Event button is clicked.
     */
    public void onRemoveUpEventClicked() throws IOException, ParseException {
        removeEvent(upAll, upListView);
    }

    /**
     * Removes the selected event from the list and database.
     * @param listView the listView to retrieve the event to remove from
     */
    public void removeEvent(List<ReservationResponse> all, ListView<String> listView)
            throws IOException, ParseException {
        String eventToRemove = listView.getSelectionModel().getSelectedItem();
        if (eventToRemove.startsWith("Room Reservation")) {
            DeleteRoomReservationRequest request = new DeleteRoomReservationRequest();
            ReservationLogic.removeReservation(all, eventToRemove, request);
            System.out.println(ServerReservationCommunication.removeRoomReservation(request));
        } else if (eventToRemove.startsWith("Bike Rental")) {
            DeleteBikeReservationRequest request = new DeleteBikeReservationRequest();
            ReservationLogic.removeReservation(all, eventToRemove, request);
            System.out.println(ServerReservationCommunication.removeBikeReservation(request));
        } else {
            RemoveEventByIdRequest request = new RemoveEventByIdRequest();
            ReservationLogic.removeReservation(all, eventToRemove, request);
            System.out.println(ServerReservationCommunication.removeCustomEvent(request));
        }
        listView.getItems().remove(eventToRemove);
    }
}
