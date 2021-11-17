package nl.tudelft.oopp.cars.entitytests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import nl.tudelft.oopp.cars.entities.User;
import org.junit.jupiter.api.Test;

public class UserTest {
    @Test
    public void equalsTest() {
        final User u1 = new User("stu@student.tudelft.nl", "student", 1234);
        final User u2 = new User("stu@tudelft.nl", "faculty", 1234);
        final User u3 = u1;
        final User u4 = new User("stu@tudelft.nl", "faculty", 1234);

        Object o = new Object();

        assertEquals(u1, u3);
        assertNotEquals(u1, u2);
        assertNotEquals(u1, o);
        assertEquals(u2, u4);
    }
}
