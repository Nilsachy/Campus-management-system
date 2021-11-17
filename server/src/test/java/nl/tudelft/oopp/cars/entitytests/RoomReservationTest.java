package nl.tudelft.oopp.cars.entitytests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Calendar;

import nl.tudelft.oopp.cars.entities.RoomReservation;

import org.junit.jupiter.api.Test;

public class RoomReservationTest {
    @Test
    public void equalsTest() {
        Calendar c1 = Calendar.getInstance();
        RoomReservation rr1 = new RoomReservation(1, c1, "ts1", "u1", "f1", "b1", "r1");
        RoomReservation rr2 = new RoomReservation(2, c1, "ts1", "u1", "f3", "b1", "r1");
        RoomReservation rr3 = new RoomReservation(1, c1, "ts1", "u1", "f1", "b1", "r1");
        Object o = new Object();
        assertEquals(rr1, rr3);
        assertNotEquals(rr1, o);
        assertNotEquals(rr1, rr2);
    }
}
