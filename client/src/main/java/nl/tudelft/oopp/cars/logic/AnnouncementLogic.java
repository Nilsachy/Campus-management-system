package nl.tudelft.oopp.cars.logic;

import java.text.ParseException;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import nl.tudelft.oopp.shared.responses.content.AnnouncementResponse;
import nl.tudelft.oopp.shared.responses.content.AnnouncementsResponse;

public class AnnouncementLogic {

    /**
     * Parses the AnnouncementsResponse into an observable list of user-friendly strings.
     * @param list the observable list to add the strings into
     * @param announcements the AnnouncementsResponse to get the information from
     */
    public static void parseResponse(ObservableList<String> list,
                                     AnnouncementsResponse announcements) {
        for (AnnouncementResponse response : announcements.getAnnouncements()) {
            if (response == null) {
                continue;
            }
            String responseString = String.format("%s: Posted on %td/%tm/%tY %tR by %s\n\n%s",
                    response.getTitle(), response.getPosted(), response.getPosted(),
                    response.getPosted(), response.getPosted(),
                    response.getUser(), response.getContent());
            list.add(responseString);
        }
    }

    /**
     * Parses the AnnouncementsResponse into an observable list of user-friendly strings.
     * @param list the observable list to add the strings into
     * @param announcements the AnnouncementsResponse to get the information from
     */
    public static void parseAdminResponse(ObservableList<String> list,
                                     AnnouncementsResponse announcements) {
        for (AnnouncementResponse response : announcements.getAnnouncements()) {
            if (response == null) {
                continue;
            }
            String responseString = String.format("%s: Posted on %td/%tm/%tY %tR by %s\n"
                            + "relevant until %td/%tm/%tY %tR"
                            + "\n\n%s",
                    response.getTitle(), response.getPosted(), response.getPosted(),
                    response.getPosted(), response.getPosted(), response.getUser(),
                    response.getRelevantUntil(), response.getRelevantUntil(),
                    response.getRelevantUntil(), response.getRelevantUntil(),
                    response.getContent());
            list.add(responseString);
        }
    }

    /**
     * Logic for sorting announcements.
     * @param announcements list of announcements
     * @param std 1: sort by title, 2: sort by time
     * @param opt 1: ascending 2: descending
     * @return sorted list of all announcements that are displayed.
     */
    public static AnnouncementsResponse sortingAnnouncementOption(
            AnnouncementsResponse announcements, int std, int opt) {

        List<AnnouncementResponse> list = announcements.getAnnouncements();
        List<AnnouncementResponse> sortedAnn;

        if (std == 1) {
            sortedAnn = AnnouncementLogic.sortByTitle(list, opt);
        } else {
            sortedAnn = AnnouncementLogic.sortByTime(list, opt);
        }
        return new AnnouncementsResponse(sortedAnn);
    }

    /**
     * Logic for sorting announcements depending on their title.
     * @param announcements list of announcements
     * @param option 1: ascending 2: descending
     * @return sorted list of all announcements that are displayed.
     */
    public static List<AnnouncementResponse> sortByTitle(
            List<AnnouncementResponse> announcements, int option) {
        int i = 1;
        if (option == 2) {
            i = -1;
        }
        int finalI = i;

        announcements.sort((a1, a2) -> {
            if (a1.getTitle().compareTo(a2.getTitle()) > 0) {
                return finalI;
            } else if (a1.getTitle().compareTo(a2.getTitle()) < 0) {
                return -1 * finalI;
            }
            return 0;
        });
        return announcements;
    }

    /**
     * Logic for sorting announcements depending on their time posted.
     * @param announcements list of announcements
     * @param option 1: ascending 2: descending
     * @return sorted list of all announcements that are displayed.
     */
    public static List<AnnouncementResponse> sortByTime(
            List<AnnouncementResponse> announcements, int option) {
        int i = 1;
        if (option == 2) {
            i = -1;
        }
        int finalI = i;

        announcements.sort((a1, a2) -> {
            if (a1.getPosted().after(a2.getPosted())) {
                return finalI;
            } else if (a1.getPosted().before(a2.getPosted())) {
                return -1 * finalI;
            }
            return 0;
        });
        return announcements;
    }

    /**
     * Fills the listView with the announcements from the AnnouncementsResponse.
     * @param type the type of user that is calling this method
     * @param announcements the AnnouncementsResponse that contains all announcements
     * @param ordering the choiceBox containing all the information needed for sorting the list
     * @param list the list that holds the parsed response
     * @param listView the listView that needs to show all elements in the list
     */
    public static void initializeAnnouncements(String type, AnnouncementsResponse announcements,
                                        ChoiceBox<String> ordering,
                                        ObservableList<String> list, ListView<String> listView) {
        list.clear();
        int[] order = GeneralLogic.parseOrder(ordering
                .getSelectionModel().getSelectedItem());
        if (announcements != null && announcements.getAnnouncements() != null) {
            announcements = sortingAnnouncementOption(announcements,
                    order[0], order[1]);
            if (type.equals("Admin")) {
                parseAdminResponse(list, announcements);
            } else if (type.equals("Regular")) {
                parseResponse(list, announcements);
            }
        }
        listView.setItems(list);
    }

    /**
     * Parses a string back into an AnnouncementResponse object.
     * @param announcement string containing all information about the announcement
     * @return an AnnouncementResponse object made from the given string
     */
    public static AnnouncementResponse parseAnnouncementToObject(
            String announcement) throws ParseException {
        AnnouncementResponse response = new AnnouncementResponse();

        // "%s", "%tD %tR by %s\n"
        // "relevant until %tD %tR"
        // "\n\n%s"
        String[] tokens = announcement.split(": Posted on ", 2);
        response.setTitle(tokens[0]);

        // "%tD %tR", "%s\n"
        // "relevant until %tD %tR"
        // "\n\n%s"
        tokens = tokens[1].split(" by ", 2);
        response.setPosted(GeneralLogic.parseDateTime("", tokens[0]));

        // "%s", "%tD %tR"
        // "\n\n%s"
        tokens = tokens[1].split("\nrelevant until ", 2);
        response.setUser(tokens[0]);

        // "%tD %tR", "%s"
        tokens = tokens[1].split("\n\n", 2);
        response.setRelevantUntil(GeneralLogic.parseDateTime("", tokens[0]));
        response.setContent(tokens[1]);

        return response;
    }

    /**
     * Looks through the AnnouncementsResponse to find the id corresponding to the announcement.
     * @param announcements the AnnouncementsResponse containing all announcements
     * @return the id of the corresponding announcement
     */
    public static long searchAnnouncementsForId(AnnouncementsResponse announcements,
                                                AnnouncementResponse response) {
        long id = -1;
        for (AnnouncementResponse announcement : announcements.getAnnouncements()) {
            if (announcement.getTitle().equals(response.getTitle())
                    && announcement.getContent().equals(response.getContent())
                    && announcement.getRelevantUntil().equals(response.getRelevantUntil())
                    && announcement.getPosted().equals(response.getPosted())) {
                id = announcement.getId();
                break;
            }
        }
        return id;
    }
}
