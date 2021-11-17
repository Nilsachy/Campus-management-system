package nl.tudelft.oopp.cars.entitytests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import nl.tudelft.oopp.cars.entities.Faculty;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class FacultyTest {

    @Test
    public void facultyEqualsTest() { // tests if the equals method works
        Faculty f1 = new Faculty("EEMCS",
                "Electrical Engineering, Mathematics, and Computer Science",
                "+31 (0)15 27 89801");
        Faculty f2 = new Faculty("EEMCS",
                "Electrical Engineering, Mathematics, and Computer Science",
                "+31 (0)15 27 89801");
        Faculty f3 = new Faculty("General",
                "General",
                "+31 (0)15 27 89800");

        assertEquals(f1, f1);
        assertEquals(f1, f2);
        assertNotEquals(f1, f3);
    }

    @Test
    public void facultyGetSetTest() {
        Faculty f1 = new Faculty("EEMCS",
                "Electrical Engineering, Mathematics, and Computer Science",
                "+31 (0)15 27 89801");

        f1.setId("NotEEMCS");
        assertEquals(f1.getId(), "NotEEMCS", "Faculty Id setter/getter works");

        f1.setName("Nothing noteworthy");
        assertEquals(f1.getName(), "Nothing noteworthy", "Faculty Name setter/getter works");

        f1.setPhone("+31 (0)15 27 89869");
        assertEquals(f1.getPhone(), "+31 (0)15 27 89869", "Faculty Phone setter/getter works");
    }
}
