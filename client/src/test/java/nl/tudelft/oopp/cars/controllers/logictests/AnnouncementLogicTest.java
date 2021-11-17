package nl.tudelft.oopp.cars.controllers.logictests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.tudelft.oopp.cars.logic.AnnouncementLogic;
import nl.tudelft.oopp.cars.logic.GeneralLogic;
import nl.tudelft.oopp.shared.responses.content.AnnouncementResponse;
import nl.tudelft.oopp.shared.responses.content.AnnouncementsResponse;
import org.junit.jupiter.api.Test;

public class AnnouncementLogicTest {

    Calendar c0 = new GregorianCalendar(2020, Calendar.MARCH, 24, 9, 0);
    Calendar c1 = new GregorianCalendar(2020, Calendar.MARCH, 28, 23, 0);
    AnnouncementResponse a0 = new AnnouncementResponse(0, c0, c1,
            "Announcement", "This is the newest news of the century!", "admin@tudelft.nl");
    AnnouncementResponse a1 = new AnnouncementResponse(1, c0, c1,
            "Title", "This is the even newer news of the millennium!", "admin@tudelft.nl");
    List<AnnouncementResponse> list = new ArrayList<>();
    ObservableList<String> ob = FXCollections.observableArrayList();

    @Test
    public void parseResponseTest() {
        list.add(a0);
        list.add(a1);
        AnnouncementsResponse response = new AnnouncementsResponse(list);
        assertEquals("[]", ob.toString());

        AnnouncementLogic.parseResponse(ob, response);
        assertEquals("[Announcement: Posted on 24/03/2020 09:00 by admin@tudelft.nl\n"
                + "\nThis is the newest news of the century!, "
                + "Title: Posted on 24/03/2020 09:00 by admin@tudelft.nl\n"
                + "\nThis is the even newer news of the millennium!]", ob.toString());
    }

    @Test
    public void parseAdminResponseTest() {
        list.add(a0);
        list.add(a1);
        AnnouncementsResponse response = new AnnouncementsResponse(list);
        assertEquals("[]", ob.toString());

        AnnouncementLogic.parseAdminResponse(ob, response);
        assertEquals("[Announcement: Posted on 24/03/2020 09:00 by admin@tudelft.nl\n"
                + "relevant until 28/03/2020 23:00"
                + "\n\nThis is the newest news of the century!, "
                + "Title: Posted on 24/03/2020 09:00 by admin@tudelft.nl\n"
                + "relevant until 28/03/2020 23:00"
                + "\n\nThis is the even newer news of the millennium!]", ob.toString());
    }

    @Test
    public void sortByTitleTest() {
        List<AnnouncementResponse> announcements = new ArrayList<AnnouncementResponse>();

        Calendar c0 = new GregorianCalendar(2020, Calendar.MARCH, 24, 9, 0);
        Calendar c1 = new GregorianCalendar(2020, Calendar.MARCH, 25, 9, 0);
        AnnouncementResponse a1 = new AnnouncementResponse(1, c0, c1, "cc", "cc", "user1");
        announcements.add(a1);

        Calendar c4 = new GregorianCalendar(2020, Calendar.DECEMBER, 24, 9, 0);
        Calendar c5 = new GregorianCalendar(2020, Calendar.DECEMBER, 25, 9, 0);
        AnnouncementResponse a3 = new AnnouncementResponse(1, c4, c5, "aa", "aa", "user1");
        announcements.add(a3);

        Calendar c2 = new GregorianCalendar(2020, Calendar.JUNE, 24, 9, 0);
        Calendar c3 = new GregorianCalendar(2020, Calendar.JUNE, 25, 9, 0);
        AnnouncementResponse a2 = new AnnouncementResponse(1, c2, c3, "bb", "bb", "user1");
        announcements.add(a2);

        announcements = AnnouncementLogic.sortByTitle(announcements,1);
        assertEquals(announcements.get(0).getTitle(), "aa");
        assertEquals(announcements.get(1).getTitle(), "bb");
        assertEquals(announcements.get(2).getTitle(), "cc");

        announcements = AnnouncementLogic.sortByTitle(announcements,2);
        assertEquals(announcements.get(0).getTitle(), "cc");
        assertEquals(announcements.get(1).getTitle(), "bb");
        assertEquals(announcements.get(2).getTitle(), "aa");
    }

    @Test
    public void sortByTimeTest() {
        List<AnnouncementResponse> announcements = new ArrayList<AnnouncementResponse>();

        Calendar c0 = new GregorianCalendar(2020, Calendar.MARCH, 24, 9, 0);
        Calendar c1 = new GregorianCalendar(2020, Calendar.MARCH, 25, 9, 0);
        AnnouncementResponse a1 = new AnnouncementResponse(1, c0, c1, "cc", "cc", "user1");
        announcements.add(a1);

        Calendar c4 = new GregorianCalendar(2020, Calendar.DECEMBER, 24, 9, 0);
        Calendar c5 = new GregorianCalendar(2020, Calendar.DECEMBER, 25, 9, 0);
        AnnouncementResponse a3 = new AnnouncementResponse(1, c4, c5, "aa", "aa", "user1");
        announcements.add(a3);

        Calendar c2 = new GregorianCalendar(2020, Calendar.JUNE, 24, 9, 0);
        Calendar c3 = new GregorianCalendar(2020, Calendar.JUNE, 25, 9, 0);
        AnnouncementResponse a2 = new AnnouncementResponse(1, c2, c3, "bb", "bb", "user1");
        announcements.add(a2);

        announcements = AnnouncementLogic.sortByTime(announcements,1);
        assertEquals(announcements.get(0).getTitle(), "cc");
        assertEquals(announcements.get(1).getTitle(), "bb");
        assertEquals(announcements.get(2).getTitle(), "aa");

        announcements = AnnouncementLogic.sortByTime(announcements,2);
        assertEquals(announcements.get(0).getTitle(), "aa");
        assertEquals(announcements.get(1).getTitle(), "bb");
        assertEquals(announcements.get(2).getTitle(), "cc");
    }

    @Test
    public void parseAnnouncementToObjectTest() throws ParseException {
        list.clear();
        list.add(a0);
        list.add(a1);
        AnnouncementsResponse result = new AnnouncementsResponse(list);
        AnnouncementLogic.parseAdminResponse(ob, result);

        AnnouncementResponse a0 = AnnouncementLogic.parseAnnouncementToObject(ob.get(0));
        assertEquals(a0.getTitle(), this.a0.getTitle());
        assertEquals(a0.getUser(), this.a0.getUser());
        assertEquals(a0.getContent(), this.a0.getContent());
        assertEquals(a0.getPosted().getTime(), this.a0.getPosted().getTime());
        assertEquals(0, a0.getId());

        AnnouncementResponse a1 = AnnouncementLogic.parseAnnouncementToObject(ob.get(1));
        assertEquals(a1.getTitle(), this.a1.getTitle());
        assertEquals(a1.getUser(), this.a1.getUser());
        assertEquals(a1.getContent(), this.a1.getContent());
        assertEquals(a1.getPosted().getTime(), this.a1.getPosted().getTime());
        assertEquals(0, a1.getId());
    }

    @Test
    public void searchAnnouncementsForIdTest() {
        list.add(a0);
        list.add(a1);
        AnnouncementsResponse announcements = new AnnouncementsResponse(list);

        long result = AnnouncementLogic.searchAnnouncementsForId(announcements, a0);
        assertEquals(0, result);

        long result2 = AnnouncementLogic.searchAnnouncementsForId(announcements, a1);
        assertEquals(1, result2);
    }
}
