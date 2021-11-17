package nl.tudelft.oopp.cars.controllers.admin;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import nl.tudelft.oopp.cars.communication.CurrentPage;
import nl.tudelft.oopp.cars.communication.ServerEntityCommunication;
import nl.tudelft.oopp.cars.controllers.MainController;
import nl.tudelft.oopp.cars.logic.AnnouncementLogic;
import nl.tudelft.oopp.cars.logic.GeneralLogic;
import nl.tudelft.oopp.shared.responses.content.AnnouncementResponse;
import nl.tudelft.oopp.shared.responses.content.AnnouncementsResponse;

public class AdminAnnouncementsInterfaceController {

    @FXML private HBox adminAnnouncements;

    @FXML private ListView<String> listView;
    @FXML private ChoiceBox<String> ordering;
    @FXML private TextField title;
    @FXML private TextArea content;
    @FXML private DatePicker date;
    @FXML private TextField time;
    @FXML private Label errorMessage;

    @FXML MainController main;

    private AnnouncementsResponse announcements;
    private ObservableList<String> list = FXCollections.observableArrayList();

    /**
     * Called to establish a connection with the mainController.
     * @param mainController the mainController calling this method
     */
    public void init(MainController mainController) {
        System.out.println("AdminAnnouncementController connected to the MainController.");
        main = mainController;
        CurrentPage.cls = this.getClass();
        CurrentPage.page = this.adminAnnouncements;
        CurrentPage.onePage = true;
    }

    /**
     * Automatically called when the page is loaded.
     */
    @FXML public void initialize() throws IOException {
        announcements = ServerEntityCommunication.getAllAnnouncements();

        AnnouncementLogic.initializeAnnouncements("Admin", announcements, ordering, list, listView);

        content.setWrapText(true);
        GeneralLogic.wrapText(listView);

        ordering.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (!(oldValue.equals(newValue))) {
                        AnnouncementLogic.initializeAnnouncements("Admin", announcements,
                                ordering, list, listView);
                    }
                });

        title.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches(".*:.*")) {
                title.setText(oldValue);
            }
        });

        content.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^[a-zA-Z0-9.!?\r\n ]*$")) {
                content.setText(oldValue);
            }
        });

        time.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9, :]*")) {
                time.setText(oldValue);
            }
        });
    }

    /**
     * Called when the add announcement button is clicked.
     */
    public void onAddAnnouncementsClicked() throws ParseException, IOException {
        String title = this.title.getText();
        String content = this.content.getText();
        String time = this.time.getText();
        String date = this.date.getEditor().getText();

        if (GeneralLogic.isValidDate(date) && GeneralLogic.isValidTime(time)) {
            Calendar calendar = GeneralLogic.parseDateTime("", date + " " + time);

            String response = ServerEntityCommunication.addAnnouncement(title, content,
                    calendar, main.getCurrentUser());
            System.out.println(response);
        } else if (!(GeneralLogic.isValidDate(date))) {
            errorMessage.setText("Please enter a valid date");
        } else if (!(GeneralLogic.isValidTime(time))) {
            errorMessage.setText("Please enter a valid time");
        }
    }

    /**
     * Called when the remove announcement button is clicked.
     */
    public void onRemoveAnnouncementsClicked() throws ParseException, IOException {
        String announcement = listView.getSelectionModel().getSelectedItem();
        AnnouncementResponse response = AnnouncementLogic.parseAnnouncementToObject(announcement);
        long id = AnnouncementLogic.searchAnnouncementsForId(announcements, response);
        System.out.println(ServerEntityCommunication.removeAnnouncement(id));
    }
}
