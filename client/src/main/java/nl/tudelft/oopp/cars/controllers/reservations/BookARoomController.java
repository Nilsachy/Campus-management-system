package nl.tudelft.oopp.cars.controllers.reservations;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.StyleClass;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import nl.tudelft.oopp.cars.communication.CurrentPage;
import nl.tudelft.oopp.cars.communication.ServerReservationCommunication;
import nl.tudelft.oopp.cars.controllers.MainController;
import nl.tudelft.oopp.cars.logic.BuildingLogic;
import nl.tudelft.oopp.cars.logic.GeneralLogic;
import nl.tudelft.oopp.cars.logic.RoomLogic;
import nl.tudelft.oopp.shared.responses.content.RoomsResponse;

public class BookARoomController {

    @FXML private HBox roomPage;

    @FXML public Label buildingName;
    @FXML private DatePicker date;
    @FXML private ChoiceBox<String> timeslot;
    @FXML private ChoiceBox<String> ordering;
    @FXML private ListView<String> listView;
    @FXML private Label errorMessage;

    @FXML MainController main;

    private ObservableList<String> list = FXCollections.observableArrayList();

    /**
     * Called to establish a connection with the mainController.
     * @param mainController the mainController calling this method
     */
    public void init(MainController mainController) {
        System.out.println("BookARoomController connected to the MainController.");
        main = mainController;
        CurrentPage.cls = this.getClass();
        CurrentPage.page = this.roomPage;
        CurrentPage.onePage = true;
    }

    /**
     * Automatically called when the page is loaded.
     */
    @FXML public void initialize() {

    }

    public void setBuildingName(String buildingName) {
        this.buildingName.setText(buildingName);
    }

    /**
     * Called when you click on the get available rooms button.
     * @throws ParseException exception thrown when parsing of the calendar fails
     */
    public void onGetAvailableRoomsClicked() throws ParseException, IOException {
        errorMessage.getStyleClass().add("negativeMessage");
        errorMessage.setText("");

        list.clear();

        int id = BuildingLogic.parseStringId(buildingName.getText());
        System.out.println(id);

        String date = this.date.getEditor().getText();
        String timeslot = this.timeslot.getSelectionModel().getSelectedItem();

        if (GeneralLogic.isValidDate(date)) {
            //errorMessage.setText("");
            Calendar calendar = GeneralLogic.parseDateTime("Date", date);
            Calendar now = Calendar.getInstance();
            now.add(Calendar.DATE, -1);
            int[] order = GeneralLogic.parseOrder(this.ordering
                    .getSelectionModel().getSelectedItem());
            if (now.before(calendar)) {
                RoomsResponse availableRooms = ServerReservationCommunication
                        .getAvailableRooms(id, calendar, timeslot);
                if (availableRooms != null && availableRooms.getRooms() != null) {
                    availableRooms = RoomLogic
                            .sortingRoomOption(availableRooms, order[0], order[1]);

                    RoomLogic.parseResponse(list, availableRooms);
                }
                listView.setItems(list);
            } else {
                errorMessage.setText("Please select a proper date and time");
            }

        } else {
            errorMessage.setText("Please select a proper date");
        }
    }

    /**
     * Called when you click on the book this room button.
     * @throws ParseException exception thrown when parsing of the calendar fails
     */
    public void onBookRoomClicked() throws ParseException, IOException {
        Calendar calendar = GeneralLogic.parseDateTime("Date", date.getEditor().getText());
        String timeslot = this.timeslot.getValue();
        String faculty = "";
        String building = buildingName.getText();
        String room = listView.getSelectionModel().getSelectedItem();
        room =  RoomLogic.getRoomNameToAddReservation(room);
        building = RoomLogic.getBuildingNameForRoomReservation(building);
        if (room != null || (!room.equals(""))) {
            ServerReservationCommunication.reserveRoom(calendar, timeslot,
                    main.getCurrentUser(), faculty, building, room);

            onGetAvailableRoomsClicked();
            errorMessage.getStyleClass().clear();
            errorMessage.getStyleClass().add("positiveMessage");
            errorMessage.setText("saved");


        }

    }
}
