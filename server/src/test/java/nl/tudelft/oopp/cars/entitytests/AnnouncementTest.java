package nl.tudelft.oopp.cars.entitytests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Calendar;

import nl.tudelft.oopp.cars.entities.Announcement;
import org.junit.jupiter.api.Test;

public class AnnouncementTest {

    @Test
    public void equalsTest() {

        Calendar c1 = Calendar.getInstance();
        //month on the db are +1 to month in set()
        c1.set(2020, 4, 1, 11, 11, 11);
        Calendar c2 = Calendar.getInstance();
        c2.set(2020, 4, 3, 11, 11, 11);
        Calendar c3 = Calendar.getInstance();

        Announcement a1 = new Announcement(c3, c1, "Title", "Content", "user");
        Announcement a2 = new Announcement(c3, c2, "Title", "Content", "user");
        Announcement a3 = new Announcement(c3, c1, "Title1", "Content", "user");
        Announcement a4 = a1;

        assertEquals(a1, a4);
        assertNotEquals(a1, a2);
        assertNotEquals(a1, a3);
    }

}
