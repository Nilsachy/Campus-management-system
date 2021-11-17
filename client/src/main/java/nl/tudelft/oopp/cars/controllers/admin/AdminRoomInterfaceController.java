package nl.tudelft.oopp.cars.controllers.admin;

import java.io.IOException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import nl.tudelft.oopp.cars.communication.CurrentPage;
import nl.tudelft.oopp.cars.communication.ServerEntityCommunication;
import nl.tudelft.oopp.cars.controllers.MainController;
import nl.tudelft.oopp.cars.logic.GeneralLogic;
import nl.tudelft.oopp.cars.logic.RoomLogic;
import nl.tudelft.oopp.shared.responses.content.RoomsResponse;

public class AdminRoomInterfaceController {

    @FXML private Button addRoomButton;
    @FXML private Button removeRoomButton;
    @FXML private HBox adminRoom;

    @FXML private ListView<String> listView;
    @FXML private ChoiceBox<String> ordering;
    @FXML private TextField faculty;
    @FXML private TextField building;
    @FXML private TextField room;
    @FXML private TextField capacity;
    @FXML private CheckBox whiteboardCheck;
    @FXML private CheckBox staffonlyCheck;

    @FXML private Label feedbackAddMessage;
    @FXML private Label feedbackRemoveMessage;

    @FXML MainController main;

    private RoomsResponse rooms;
    private ObservableList<String> list = FXCollections.observableArrayList();

    /**
     * Called to establish a connection with the mainController.
     * @param mainController the mainController calling this method
     */
    public void init(MainController mainController) {
        System.out.println("AdminRoomController connected to the MainController.");
        main = mainController;
        CurrentPage.cls = this.getClass();
        CurrentPage.page = this.adminRoom;
        CurrentPage.onePage = true;
    }

    /**
     * Automatically called when the page is loaded.
     */
    @FXML public void initialize() throws IOException {
        rooms = ServerEntityCommunication.getAllRooms();
        RoomLogic.sortingRoomOption(rooms, 2, 1);
        RoomLogic.parseAdminResponse(list, rooms);
        listView.setItems(list);

        ordering.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (!(oldValue.equals(newValue))) {
                        list.clear();
                        int[] order = GeneralLogic.parseOrder(ordering.getSelectionModel()
                                .getSelectedItem());
                        RoomLogic.sortingRoomOption(rooms, order[0], order[1]);
                        RoomLogic.parseAdminResponse(list, rooms);
                    }
                });

        addListener(capacity);
        addListener(building);
    }

    /**
     * Makes sure that the things typed into the textField are numbers.
     * @param text the textfield to listen to
     */
    public void addListener(TextField text) {
        text.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9]*")) {
                text.setText(oldValue);
            }
        });
    }

    /**
     * Called when the Add Room button is clicked, adds a room to the database.
     */
    public void onAddRoomClicked() throws IOException {


        this.faculty.textProperty().addListener((observable, oldValue, newValue) -> {
            addRoomButton.setTextFill(Color.BLACK);
            addRoomButton.setText("Add Room");
            if (!newValue.equals("field is empty. not saved")) {
                this.faculty.getStyleClass().add("blackText2");
                if (oldValue.equals("field is empty. not saved")) {
                    Platform.runLater(() -> {
                        this.faculty.setText("");
                    });
                }
            } else {
                this.faculty.getStyleClass().add("negativeMessage");
            }
        });

        this.building.textProperty().addListener((observable, oldValue, newValue) -> {
            addRoomButton.setTextFill(Color.BLACK);
            addRoomButton.setText("Add Room");
            if (!newValue.equals("0000")) {
                this.building.getStyleClass().add("blackText2");
                if (oldValue.equals("0000")) {
                    Platform.runLater(() -> {
                        this.building.setText("");
                    });
                }
            } else {
                this.building.getStyleClass().add("negativeMessage");
            }
        });

        this.room.textProperty().addListener((observable, oldValue, newValue) -> {
            addRoomButton.setTextFill(Color.BLACK);
            addRoomButton.setText("Add Room");
            if (!newValue.equals("field is empty. not saved")) {
                this.room.getStyleClass().add("blackText2");
                if (oldValue.equals("field is empty. not saved")) {
                    Platform.runLater(() -> {
                        this.room.setText("");
                    });
                }
            } else {
                this.room.getStyleClass().add("negativeMessage");
            }
        });
        this.capacity.textProperty().addListener((observable, oldValue, newValue) -> {
            addRoomButton.setTextFill(Color.BLACK);
            addRoomButton.setText("Add Room");
            if (!newValue.equals("0")) {
                this.capacity.getStyleClass().add("blackText2");
                if (oldValue.equals("0")) {
                    Platform.runLater(() -> {
                        this.capacity.setText("");
                    });
                }
            } else {
                this.capacity.getStyleClass().add("negativeMessage");
            }
        });

        String faculty = this.faculty.getText();
        String buildingId = this.building.getText();
        String room = this.room.getText();
        String capacityInString = this.capacity.getText();

        String[] setTextMsg = RoomLogic.checkEmptyField(faculty,
                buildingId, room, capacityInString);

        this.faculty.setText(setTextMsg[0]);
        this.building.setText(setTextMsg[1]);
        this.room.setText(setTextMsg[2]);
        this.capacity.setText(setTextMsg[3]);

        boolean whiteboard = this.whiteboardCheck.isSelected();
        boolean staffonly = this.staffonlyCheck.isSelected();

        this.whiteboardCheck.setSelected(whiteboard);
        this.staffonlyCheck.setSelected(staffonly);

        if (!setTextMsg[4].equals("false")) {
            int building = Integer.parseInt(buildingId);
            int capacity = Integer.parseInt(capacityInString);

            ServerEntityCommunication.addRoom(faculty,
                    building, room, capacity, whiteboard, staffonly);

            rooms = ServerEntityCommunication.getAllRooms();

            list.clear();
            RoomLogic.sortingRoomOption(rooms, 2, 1);
            RoomLogic.parseAdminResponse(list, rooms);
            listView.setItems(list);

            this.whiteboardCheck.setSelected(false);
            this.staffonlyCheck.setSelected(false);

            feedbackAddMessage.getStyleClass().clear();
            feedbackAddMessage.getStyleClass().add("positiveMessage");
            feedbackAddMessage.setText("saved");

        }
    }


    /**
     * Called when the Remove Room button is clicked, removes a room from the database.
     */
    public void onRemoveRoomClicked() throws IOException {
        String roomToDelete = listView.getSelectionModel().getSelectedItem();

        listView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (oldValue.equals(roomToDelete)) {
                        Platform.runLater(() -> {
                            feedbackRemoveMessage.getStyleClass().clear();
                            feedbackRemoveMessage.getStyleClass().add("positiveMessage");
                            feedbackRemoveMessage.setText("removed successfully");
                        });
                    }
                });
        if (roomToDelete != null && roomToDelete.length() > 0) {
            int idToDelete = RoomLogic.getRoomIdToDelete(roomToDelete);
            ServerEntityCommunication.removeRoom(idToDelete);
            listView.getItems().remove(roomToDelete);
        }
    }
}
