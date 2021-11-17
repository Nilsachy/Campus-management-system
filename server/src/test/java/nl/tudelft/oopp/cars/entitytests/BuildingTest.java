package nl.tudelft.oopp.cars.entitytests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import nl.tudelft.oopp.cars.entities.Building;
import org.junit.jupiter.api.Test;

public class BuildingTest {
    @Test
    public void equalsTest() {
        Building b1 = new Building(33, "n1", "f1", "a1");
        Building b2 = new Building(12, "n2", "f2", "a2");
        Building b3 = new Building(33, "n1", "f1", "a1");
        Object o = new Object();
        assertEquals(b1, b3);
        assertNotEquals(b1, b2);
        assertNotEquals(b1, o);
    }
}
