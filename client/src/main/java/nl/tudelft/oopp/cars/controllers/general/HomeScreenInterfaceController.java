package nl.tudelft.oopp.cars.controllers.general;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import nl.tudelft.oopp.cars.communication.CurrentPage;
import nl.tudelft.oopp.cars.communication.ServerEntityCommunication;
import nl.tudelft.oopp.cars.communication.ServerReservationCommunication;
import nl.tudelft.oopp.cars.communication.ServerUserCommunication;
import nl.tudelft.oopp.cars.controllers.MainController;
import nl.tudelft.oopp.cars.logic.AnnouncementLogic;
import nl.tudelft.oopp.cars.logic.GeneralLogic;
import nl.tudelft.oopp.cars.logic.ReservationLogic;
import nl.tudelft.oopp.shared.responses.content.AnnouncementsResponse;
import nl.tudelft.oopp.shared.responses.content.CustomEventsResponse;
import nl.tudelft.oopp.shared.responses.content.ReservationResponse;

public class HomeScreenInterfaceController {

    @FXML private HBox homeScreen;

    @FXML private ChoiceBox<String> announcementsOrdering;
    @FXML private ListView<String> announcementsListView;
    @FXML private ChoiceBox<String> eventsOrdering;
    @FXML private ListView<String> eventsListView;

    @FXML MainController main;

    private AnnouncementsResponse announcements;
    private List<ReservationResponse> reservations;
    private CustomEventsResponse events;
    private List<ReservationResponse> all = new ArrayList<>();

    private ObservableList<String> announcementsList = FXCollections.observableArrayList();
    private ObservableList<String> eventsList = FXCollections.observableArrayList();


    /**
     * Called to establish a connection with the mainController.
     * @param mainController the mainController calling this method
     */
    public void init(MainController mainController) {
        System.out.println("HomeScreenController connected to the MainController.");
        main = mainController;
        CurrentPage.cls = this.getClass();
        CurrentPage.page = this.homeScreen;
        CurrentPage.onePage = true;
    }

    /**
     * Automatically called when the page is loaded.
     */
    @FXML public void initialize() throws IOException {
        announcements = ServerEntityCommunication.getAllAnnouncements();
        String currentUser = ServerUserCommunication.getThisUser();

        GeneralLogic.wrapText(announcementsListView);

        reservations = ServerReservationCommunication.getReservations(currentUser, "active");
        events = ServerReservationCommunication.getCustomEvents(currentUser, "active");
        all.addAll(reservations);
        all.addAll(events.getEvents());

        AnnouncementLogic.initializeAnnouncements("Regular", announcements, announcementsOrdering,
                announcementsList, announcementsListView);
        ReservationLogic.initializeEvents(all, eventsOrdering, eventsList, eventsListView);

        addListener("Announcements", announcementsOrdering);
        addListener("Events", eventsOrdering);
    }

    /**
     * Adds a listener to the given choiceBox.
     * @param type determines what happens when a choiceBox changes
     * @param ordering the choiceBox that should be listened to for changes
     */
    public void addListener(String type, ChoiceBox<String> ordering) {
        ordering.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (!(oldValue.equals(newValue))) {
                        if (type.equals("Announcements")) {
                            AnnouncementLogic.initializeAnnouncements("Regular", announcements,
                                    ordering, announcementsList, announcementsListView);
                        } else if (type.equals("Events")) {
                            ReservationLogic.initializeEvents(all, ordering,
                                    eventsList, eventsListView);
                        }
                    }
                });
    }

    /**
     * Called when the Book a Room button is clicked, loads the book room page.
     * @throws IOException called when it fails to load the reserve room interface fxml
     */
    public void onBookARoomClicked() throws IOException {
        main.loadView("/reservations/BookRoomInterface.fxml");
    }

    /**
     * Called when the Rent a Bike button is clicked, loads the rent bike page.
     * @throws IOException called when it fails to load the rent bike interface fxml
     */
    public void onRentABikeClicked() throws IOException {
        main.loadView("/reservations/rentABikeInterface.fxml");
    }

    /**
     * Called when the Manipulate Events button is clicked, loads the manipulate events page.
     * @throws IOException called when it fails to load the manipulate events interface fxml
     */
    public void onManipulateEventsClicked() throws IOException {
        main.loadView("/general/manipulateEventsInterface.fxml");
    }

    /**
     * Called when the Add Event button is clicked, loads the event page.
     * @throws IOException called when it fails to load the event interface fxml
     */
    public void onAddEventClicked() throws IOException {
        main.loadView("/general/customEventInterface.fxml");
    }
}
