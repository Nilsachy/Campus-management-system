package nl.tudelft.oopp.cars.controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.oopp.cars.communication.ServerUserCommunication;
import nl.tudelft.oopp.cars.controllers.admin.AdminAnnouncementsInterfaceController;
import nl.tudelft.oopp.cars.controllers.admin.AdminBikeStorageInterfaceController;
import nl.tudelft.oopp.cars.controllers.admin.AdminBuildingInterfaceController;
import nl.tudelft.oopp.cars.controllers.admin.AdminPanelInterfaceController;
import nl.tudelft.oopp.cars.controllers.admin.AdminRoomInterfaceController;
import nl.tudelft.oopp.cars.controllers.general.AccountInterfaceController;
import nl.tudelft.oopp.cars.controllers.general.CustomEventInterfaceController;
import nl.tudelft.oopp.cars.controllers.general.HomeScreenInterfaceController;
import nl.tudelft.oopp.cars.controllers.general.ManipulateEventsInterfaceController;
import nl.tudelft.oopp.cars.controllers.reservations.BookARoomController;
import nl.tudelft.oopp.cars.controllers.reservations.BookRoomInterfaceController;
import nl.tudelft.oopp.cars.controllers.reservations.RentABikeController;
import nl.tudelft.oopp.shared.responses.content.BikeReservationResponse;
import nl.tudelft.oopp.shared.responses.content.CustomEventResponse;
import nl.tudelft.oopp.shared.responses.content.RoomReservationResponse;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MainController {

    @FXML private HBox breadcrumbs;
    @FXML private HBox child;
    @FXML private Pane goToAdminContainer;

    // Controllers of the general views.
    @FXML HomeScreenInterfaceController homeScreenController;
    @FXML AccountInterfaceController accountPageController;

    // Controllers of the reservation views.
    @FXML BookRoomInterfaceController bookRoomController;
    @FXML BookARoomController bookARoomController;
    @FXML RentABikeController rentABikeController;
    @FXML CustomEventInterfaceController customEventController;
    @FXML ManipulateEventsInterfaceController manipulateEventsController;

    // Controllers of the admin views.
    @FXML AdminPanelInterfaceController adminPanelController;
    @FXML AdminRoomInterfaceController adminRoomController;
    @FXML AdminBuildingInterfaceController adminBuildingController;
    @FXML AdminBikeStorageInterfaceController adminBikeStorageController;
    @FXML AdminBuildingInterfaceController adminAnnouncementsController;

    private String currentUser = "";
    private String currentRole = "";
    private String building;
    private boolean autoFill = false;
    private RoomReservationResponse roomReservation = new RoomReservationResponse();
    private BikeReservationResponse bikeReservation = new BikeReservationResponse();
    private CustomEventResponse customEvent = new CustomEventResponse();

    /**
     * Called upon loading the page, initializes the MainController.
     * @throws IOException called when it fails to load the home screen interface fxml
     */
    @FXML public void initialize() throws IOException {
        System.out.println("MainController is up and running.");

        currentUser = ServerUserCommunication.getThisUser();
        currentRole = ServerUserCommunication.getThisRole();

        // init goto admin panel
        if (currentRole.equals("admin")) {
            Button goToAdminButton = new Button("Go to admin panel");
            goToAdminButton.setOnAction(value ->  {
                try {
                    onAdminPanelClicked();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            goToAdminContainer.getChildren().add(goToAdminButton);
        }

        // init home page
        onHomeLinkClicked();
    }

    /**
     * Called when the Home link is clicked, loads the home screen.
     * @throws IOException called when it fails to load the home screen interface fxml
     */
    public void onHomeLinkClicked() throws IOException {
        loadView("/general/homeScreenInterface.fxml");
    }

    /**
     * Called when the Profile Picture is clicked, loads the account screen.
     * @throws IOException called when it fails to load the account interface fxml
     */
    public void onProfilePicClicked() throws IOException {
        loadView("/general/accountInterface.fxml");
    }

    /**
     * Called when the Admin Panel button is clicked, loads the admin panel.
     * @throws IOException called when it fails to load the admin panel interface fxml
     */
    public void onAdminPanelClicked() throws IOException {
        loadView("/admin/adminPanelInterface.fxml");
    }

    /**
     * Loads a new page in the same scene based on the input.
     * @param url the url that decides which page is loaded
     * @throws IOException called when it fails to load the interface fxml
     */
    public void loadView(String url) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        HBox pane = fxmlLoader.load(getClass().getResource(url).openStream());
        Object controller = fxmlLoader.getController();
        child.getChildren().setAll(pane);

        if (url.contains("general")) {
            checkGeneral(controller);
        } else if (url.contains("reservations")) {
            checkReservations(controller);
        } else if (url.contains("admin")) {
            checkAdmin(controller);
        }
    }

    /**
     * Initializes the mainController on the page that is loaded.
     * @param controller the controller corresponding to the page
     */
    public void checkGeneral(Object controller) {
        if (controller instanceof HomeScreenInterfaceController) {
            HomeScreenInterfaceController c = (HomeScreenInterfaceController) controller;
            c.init(this);
            breadcrumbs.getChildren().clear();
        } else if (controller instanceof AccountInterfaceController) {
            AccountInterfaceController c = (AccountInterfaceController) controller;
            c.init(this);
            if (!(breadcrumbs.getChildren().isEmpty())) {
                Button b = (Button) breadcrumbs.getChildren()
                        .get(breadcrumbs.getChildren().size() - 1);
                if (!(b.getText().equals("Account"))) {
                    addBreadNoAction("Account");
                }
            } else {
                addBreadNoAction("Account");
            }
        } else if (controller instanceof CustomEventInterfaceController) {
            CustomEventInterfaceController c = (CustomEventInterfaceController) controller;
            c.init(this);
            addBreadNoAction("Add Event");
        } else if (controller instanceof ManipulateEventsInterfaceController) {
            ManipulateEventsInterfaceController c
                    = (ManipulateEventsInterfaceController) controller;
            c.init(this);
            addBreadNoAction("Manipulate Events");
        }
    }

    /**
     * Initializes the mainController on the page that is loaded.
     * @param controller the controller corresponding to the page
     */
    public void checkReservations(Object controller) {
        if (controller instanceof BookRoomInterfaceController) {
            BookRoomInterfaceController b = (BookRoomInterfaceController) controller;
            b.init(this);
            breadcrumbs.getChildren().clear();
            addBreadAction("/reservations/BookRoomInterface.fxml", controller, "Find a Building");
        } else if (controller instanceof BookARoomController) {
            BookARoomController b = (BookARoomController) controller;
            b.init(this);
            b.setBuildingName(building);
            addBreadNoAction("Book a Room");
        } else if (controller instanceof RentABikeController) {
            RentABikeController b = (RentABikeController) controller;
            b.init(this);
            addBreadNoAction("Rent a Bike");
        }
    }

    /**
     * Initializes the mainController on the page that is loaded.
     * @param controller the controller corresponding to the page
     */
    public void checkAdmin(Object controller) {
        if (controller instanceof AdminPanelInterfaceController) {
            AdminPanelInterfaceController a = (AdminPanelInterfaceController) controller;
            a.init(this);
            breadcrumbs.getChildren().clear();
            addBreadAction("/admin/adminPanelInterface.fxml", controller, "Admin Panel");
        } else if (controller instanceof AdminRoomInterfaceController) {
            AdminRoomInterfaceController a = (AdminRoomInterfaceController) controller;
            a.init(this);
            addBreadNoAction("Manipulate Rooms");
        } else if (controller instanceof AdminBuildingInterfaceController) {
            AdminBuildingInterfaceController a = (AdminBuildingInterfaceController) controller;
            a.init(this);
            addBreadNoAction("Manipulate Buildings");
        } else if (controller instanceof AdminBikeStorageInterfaceController) {
            AdminBikeStorageInterfaceController a
                    = (AdminBikeStorageInterfaceController) controller;
            a.init(this);
            addBreadNoAction("Manipulate Bike Storages");
        } else if (controller instanceof AdminAnnouncementsInterfaceController) {
            AdminAnnouncementsInterfaceController a
                    = (AdminAnnouncementsInterfaceController) controller;
            a.init(this);
            addBreadNoAction("Manipulate Announcements");
        }
    }

    /**
     * Creates and appends a new breadcrumb to the list of breadcrumbs, without an actionEvent.
     * @param name the name of the breadcrumb
     */
    public void addBreadNoAction(String name) {
        breadcrumbs.getChildren().add(new Text(" > "));
        Button button = new Button(name);
        button.getStyleClass().add("breadcrumbButton");
        breadcrumbs.getChildren().add(button);
    }

    /**
     * Creates and appends a new breadcrumb to the list of breadcrumbs, with an actionEvent.
     * @param url the url where the button should go when being pressed
     * @param controller the controller corresponding to the page
     * @param name the name of the breadcrumb
     */
    public void addBreadAction(String url, Object controller, String name) {
        breadcrumbs.getChildren().add(new Text(" > "));
        Button button = new Button(name);
        button.getStyleClass().add("breadcrumbButton");
        button.setOnAction(e -> {
            try {
                loadView(url);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        breadcrumbs.getChildren().add(button);
    }
}
