package nl.tudelft.oopp.cars.entitytests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import nl.tudelft.oopp.cars.entities.Room;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class RoomTest {



    @Test
    public void roomEqualsTest() {
        Room r1 = new Room("EEMCS", 23, "0.0", 10, true, false);
        Room r2 = new Room("EEMCS1", 231, "0.01", 101, true, false);
        Room r3 = r1;
        Room r4 = new Room("EEMCS1", 231, "0.01", 101, true, false);

        assertEquals(r1, r3);
        assertNotEquals(r1, r2);
        assertNotEquals(r1, r4);
    }

    @Test
    public void getterSetterIdTest() {
        Room r1 = new Room("EEMCS", 23, "0.0", 10, true, false);

        r1.setId(11);

        assertEquals(r1.getId(), 11);
    }

    @Test
    public void getterSetterFacultyTest() {
        Room r1 = new Room("EEMCS", 23, "0.0", 10, true, false);

        assertEquals(r1.getFaculty(), "EEMCS");

        r1.setFaculty("1234");
        assertEquals("1234", r1.getFaculty());
    }

    @Test
    public void getterSetterBuildingTest() {
        Room r1 = new Room("EEMCS", 23, "0.0", 10, true, false);

        assertEquals(r1.getBuilding(), 23);

        r1.setBuilding(1234);
        assertEquals(1234, r1.getBuilding());
    }

    @Test
    public void getterSetterRoomTest() {
        Room r1 = new Room("EEMCS", 23, "0.0", 10, true, false);

        assertEquals(r1.getRoom(), "0.0");

        r1.setRoom("1234");
        assertEquals("1234", r1.getRoom());
    }

    @Test
    public void getterSetterCapacityTest() {
        Room r1 = new Room("EEMCS", 23, "0.0", 10, true, false);

        assertEquals(r1.getCapacity(), 10);

        r1.setCapacity(1234);
        assertEquals(r1.getCapacity(), 1234);
    }

    @Test
    public void getterSetterWhiteboardTest() {
        Room r1 = new Room("EEMCS", 23, "0.0", 10, true, false);

        assertTrue(r1.hasWhiteboard());

        r1.setWhiteboard(false);
        assertFalse(r1.hasWhiteboard());
    }

    @Test
    public void getterSetterStaffOnly() {
        Room r1 = new Room("EEMCS", 23, "0.0", 10, true, false);

        assertFalse(r1.isStaffOnly());
        
        r1.setStaffOnly(true);
        assertTrue(r1.isStaffOnly());
    }
}

