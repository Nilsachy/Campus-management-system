package nl.tudelft.oopp.cars.entitytests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import nl.tudelft.oopp.cars.entities.BikeStorage;
import org.junit.jupiter.api.Test;

public class BikeStorageTest {

    @Test
    public void equalsTest() {
        BikeStorage bs1 = new BikeStorage("f1", 30);
        BikeStorage bs2 = new BikeStorage("f3", 30);
        BikeStorage bs3 = new BikeStorage("f1", 30);
        Object o = new Object();
        assertEquals(bs1, bs3);
        assertNotEquals(bs1, o);
        assertNotEquals(bs1, bs2);
    }
}
