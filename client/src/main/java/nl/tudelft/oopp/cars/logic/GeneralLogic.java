package nl.tudelft.oopp.cars.logic;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import nl.tudelft.oopp.cars.communication.CurrentPage;

public class GeneralLogic {

    /**
     * Makes sure that you don't need to scroll to see all the text.
     * @param listView the list that needs its text wrapped
     */
    public static void wrapText(ListView<String> listView) {
        listView.setCellFactory(list -> new ListCell<>() {
            {
                Text text = new Text();
                text.wrappingWidthProperty().bind(listView.widthProperty().subtract(20));
                text.textProperty().bind(itemProperty());

                setPrefWidth(0);
                setGraphic(text);
            }
        });
    }

    /**
     * Confirms is a given string has the correct date format.
     * @param date the string containing the date to be validated
     * @return whether or not the date is valid
     */
    public static boolean isValidDate(String date) {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        format.setLenient(false);
        try {
            format.parse(date);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    /**
     * Confirms is a given string has the correct time format.
     * @param time the string containing the time to be validated
     * @return whether or not the time is valid
     */
    public static boolean isValidTime(String time) {
        DateFormat format = new SimpleDateFormat("HH:mm");
        format.setLenient(false);
        try {
            format.parse(time);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    /**
     * Parses the date and time from a string into a Calendar object.
     * @param format the format to use when parsing the given string
     * @param information the string containing the date and time
     * @return a calendar object containing the information from the string
     */
    public static Calendar parseDateTime(String format, String information) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        DateFormat formatter;
        switch (format) {
            case "Date":
                formatter = new SimpleDateFormat("dd/MM/yyyy");
                break;
            case "Time":
                formatter = new SimpleDateFormat("HH:mm");
                break;
            default:
                formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                break;
        }
        Date date = formatter.parse(information);
        calendar.setTime(date);
        return calendar;
    }

    /**
     * Checks if the given Calendars have equal year, month and day.
     * @param type determines how many of the fields to compare
     * @param c1 one of the Calendars to compare
     * @param c2 the other Calendar to compare
     * @return a boolean indicating whether or not the Calendars are equal
     */
    public static boolean compareCalendars(String type, Calendar c1, Calendar c2) {
        boolean result =  c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
        if (!type.equals("Date")) {
            result = result && c1.get(Calendar.HOUR_OF_DAY) == c2.get(Calendar.HOUR_OF_DAY)
                    && c1.get(Calendar.MINUTE) == c2.get(Calendar.MINUTE);
        }
        return result;
    }

    /**
     * Parses the given string to see how the user wants to sort their list.
     * @param s a string containing the selected choice for ordering
     * @return an array of integers containing the choices for ordering
     */
    public static int[] parseOrder(String s) {
        int[] result = new int[2];
        if (s.contains("title") || s.contains("name")) {
            result[0] = 1;
        } else {
            result[0] = 2;
        }
        if (s.contains("ascending") || s.contains("most")) {
            result[1] = 1;
        } else {
            result[1] = 2;
        }
        return result;
    }

    /**
     * When no request has been made in a while, the session expires.
     * Return to login page.
     *
     * @throws IOException exception thrown when the loginInterface.fxml file fails to load
     */
    public static void sessionDenial(String sessionError) throws IOException {
        if (sessionError == null) {
            return;
        } else if (!sessionError.equals("No Session.")) {
            return;
        } else if (!CurrentPage.onePage) {
            return;
        }

        CurrentPage.onePage = false;

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(CurrentPage.cls.getResource("/general/loginInterface.fxml"));

        Parent root = loader.load();

        Stage main = new Stage();
        main.setTitle("Login");
        main.setScene(new Scene(root));
        main.show();

        Stage stage = (Stage) CurrentPage.page.getScene().getWindow();
        stage.close();
    }
}
