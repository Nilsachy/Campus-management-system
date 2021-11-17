package nl.tudelft.oopp.cars.logic;

public class ErrorMessageLogic {

    /**
     * Parses the input to display the corresponding error message.
     * @param password the entered password
     * @return the appropriate error message corresponding to the input
     */
    public static String accountSettingsMessage(String password) {
        if (password == null || password.equals("")) {
            return "Please enter a non-empty password";
        }
        return "";
    }

    /**
     * Parses the input to display the corresponding error message.
     * @param event the selected event
     * @return the appropriate error message corresponding to the input
     */
    public static String accountEventsMessage(String event) {
        if (event == null || event.equals("")) {
            return "Please select an event from the list";
        }
        return "";
    }

    /**
     * Parses the input to display the corresponding error message.
     * @param fromDay date from which to rent a bike
     * @param toDay date until which to rent a bike
     * @param fromTime time from which to rent a bike
     * @param toTime time until which to rent a bike
     * @return the appropriate error message corresponding to the input
     */
    public static String bikeFromToMessage(String fromDay, String toDay,
                                            String fromTime, String toTime) {
        if (fromTime == null || fromTime.equals("")) {
            return "Please enter a time to rent from";
        } else if (toTime == null || toTime.equals("")) {
            return "Please enter a time to rent to";
        } else if (fromDay == null || fromDay.equals("")) {
            return "Please enter a date to rent from";
        } else if (toDay == null || toDay.equals("")) {
            return "Please enter a date to rent to";
        }
        return "";
    }

    /**
     * Parses the input to display the corresponding error message.
     * @param departBikeStorage the bike storage to pick up the bike
     * @param returnBikeStorage the bike storage to drop off the bike
     * @return the appropriate error message corresponding to the input
     */
    public static String bikeRentMessage(String departBikeStorage, String returnBikeStorage) {
        if (departBikeStorage == null || departBikeStorage.equals("")) {
            return "Please select a depart building";
        } else if (returnBikeStorage == null || returnBikeStorage.equals("")) {
            return "Please select a return building";
        }
        return "";
    }
}
