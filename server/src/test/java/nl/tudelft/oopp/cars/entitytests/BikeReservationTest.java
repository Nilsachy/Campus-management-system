package nl.tudelft.oopp.cars.entitytests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Calendar;
import nl.tudelft.oopp.cars.entities.BikeReservation;
import org.junit.jupiter.api.Test;

public class BikeReservationTest {

    @Test
    public void equalsTest() {
        Calendar c1 = Calendar.getInstance();
        //month on the db are +1 to month in set()
        c1.set(2020, 3, 1, 11, 11, 11);

        Calendar c2 = Calendar.getInstance();
        c2.set(2020, 3, 3, 11, 11, 11);

        BikeReservation br1 = new BikeReservation("f1", "u1","f2", c1, c2);
        BikeReservation br2 = new BikeReservation("f1", "u3", "f2", c1, c2);
        BikeReservation br3 = new BikeReservation("f1", "u3","f2", c1, c2);

        BikeReservation br4 = br1;

        assertEquals(br1, br4);

        assertFalse(br1.equals(br2));
        assertFalse(br1.equals(br3));
    }
}
