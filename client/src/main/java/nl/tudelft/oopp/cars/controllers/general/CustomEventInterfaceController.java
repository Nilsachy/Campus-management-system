package nl.tudelft.oopp.cars.controllers.general;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import nl.tudelft.oopp.cars.communication.ServerEntityCommunication;
import nl.tudelft.oopp.cars.communication.ServerUserCommunication;
import nl.tudelft.oopp.cars.controllers.MainController;
import nl.tudelft.oopp.cars.logic.GeneralLogic;


public class CustomEventInterfaceController {

    @FXML private HBox customEvents;
    @FXML private TextField title;
    @FXML private TextField address;
    @FXML private DatePicker dateFrom;
    @FXML private DatePicker dateTo;
    @FXML private TextField timeFrom;
    @FXML private TextField timeTo;
    @FXML private TextArea content;
    @FXML private Label errorMessage;
    @FXML private ListView<String> listView;

    @FXML MainController main;

    /**
     * Called to establish a connection with the mainController.
     * @param mainController the mainController calling this method
     */
    public void init(MainController mainController) {
        System.out.println("CustomEventController connected to the MainController.");
        main = mainController;
    }

    /**
     * Automatically called when the page is loaded.
     */
    @FXML public void initialize() {
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

        address.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^[a-zA-Z0-9., ]*$")) {
                address.setText(oldValue);
            }
        });

        timeFrom.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9:]*")) {
                timeFrom.setText(oldValue);
            }
        });

        timeTo.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9:]*")) {
                timeTo.setText(oldValue);
            }
        });
    }

    /**
     * Called when the add announcement button is clicked.
     */

    public void onAddCustomEventClicked() throws ParseException, IOException {
        String title = this.title.getText();
        String content = this.content.getText();
        String address = this.address.getText();
        String timeFrom = this.timeFrom.getText();
        String timeTo = this.timeTo.getText();
        String dateFrom = this.dateFrom.getEditor().getText();
        String dateTo = this.dateTo.getEditor().getText();

        if (GeneralLogic.isValidDate(dateFrom) && GeneralLogic.isValidDate(dateTo)
                && GeneralLogic.isValidTime(timeFrom) && GeneralLogic.isValidTime(timeTo)) {
            Calendar start = GeneralLogic.parseDateTime("", dateFrom + " " + timeFrom);
            Calendar end = GeneralLogic.parseDateTime("", dateTo + " " + timeTo);

            String response = ServerEntityCommunication.addCustomEvent(title, content, address,
                    start, end, ServerUserCommunication.getThisUser());
            System.out.println(response);

        } else if (!(GeneralLogic.isValidDate(dateFrom)) || !(GeneralLogic.isValidDate(dateTo))) {
            errorMessage.setText("Please enter a valid date");
        } else if (!(GeneralLogic.isValidTime(timeFrom)) || !(GeneralLogic.isValidDate(timeTo))) {
            errorMessage.setText("Please enter a valid time");
        }
    }
}
