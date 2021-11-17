package nl.tudelft.oopp.cars.controllers.logictests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.util.Calendar;

import nl.tudelft.oopp.cars.logic.GeneralLogic;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GeneralLogicTest {

    @Test
    public void isValidDateTest() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            GeneralLogic.isValidDate(null);
        });

        assertFalse(GeneralLogic.isValidDate(""));
        assertFalse(GeneralLogic.isValidDate("01"));
        assertFalse(GeneralLogic.isValidDate("01/01"));
        assertFalse(GeneralLogic.isValidDate("01-01-2001"));
        assertFalse(GeneralLogic.isValidDate("01.01.2001"));
        assertTrue(GeneralLogic.isValidDate("01/01/2001"));
    }

    @Test
    public void isValidTimeTest() {
        //TODO: Implement test
    }

    @Test
    public void parseDateNullTest() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            GeneralLogic.parseDateTime("", null);
        });
    }

    @Test
    public void parseDateFailTest() {
        Assertions.assertThrows(ParseException.class, () -> {
            GeneralLogic.parseDateTime("Date","01");
        });
        Assertions.assertThrows(ParseException.class, () -> {
            GeneralLogic.parseDateTime("Date","01/01");
        });
        Assertions.assertThrows(ParseException.class, () -> {
            GeneralLogic.parseDateTime("Date","01/2001");
        });
        Assertions.assertThrows(ParseException.class, () -> {
            GeneralLogic.parseDateTime("Date","01.01.2001");
        });
        Assertions.assertThrows(ParseException.class, () -> {
            GeneralLogic.parseDateTime("Date","01-01-2001");
        });
    }

    @Test
    public void parseDateSuccessTest() throws ParseException {
        Calendar c = GeneralLogic.parseDateTime("Date", "28/03/2020");
        assertEquals(c.getTime(), c.getTime());

        Calendar c1 = Calendar.getInstance();
        c1.set(2020, Calendar.MARCH, 28);

        assertNotNull(c);
        assertEquals(c.get(Calendar.DAY_OF_YEAR), c1.get(Calendar.DAY_OF_YEAR));
        assertEquals(c.get(Calendar.MONTH), c1.get(Calendar.MONTH));
        assertEquals(c.get(Calendar.YEAR), c1.get(Calendar.YEAR));
    }

    @Test
    public void parseOrderTest() {
        String s1 = "Sort by title, ascending";
        assertEquals(1, GeneralLogic.parseOrder(s1)[0]);
        assertEquals(1, GeneralLogic.parseOrder(s1)[1]);

        String s2 = "Sort by title, descending";
        assertEquals(1, GeneralLogic.parseOrder(s2)[0]);
        assertEquals(2, GeneralLogic.parseOrder(s2)[1]);

        String s3 = "Sort by most recent";
        assertEquals(2, GeneralLogic.parseOrder(s3)[0]);
        assertEquals(1, GeneralLogic.parseOrder(s3)[1]);

        String s4 = "Sort by least recent";
        assertEquals(2, GeneralLogic.parseOrder(s4)[0]);
        assertEquals(2, GeneralLogic.parseOrder(s4)[1]);

        String s5 = "Sort by name, ascending";
        assertEquals(1, GeneralLogic.parseOrder(s5)[0]);
        assertEquals(1, GeneralLogic.parseOrder(s5)[1]);

        String s6 = "Sort by name, descending";
        assertEquals(1, GeneralLogic.parseOrder(s6)[0]);
        assertEquals(2, GeneralLogic.parseOrder(s6)[1]);

        String s7 = "Sort by capacity, ascending";
        assertEquals(2, GeneralLogic.parseOrder(s7)[0]);
        assertEquals(1, GeneralLogic.parseOrder(s7)[1]);

        String s8 = "Sort by capacity, descending";
        assertEquals(2, GeneralLogic.parseOrder(s8)[0]);
        assertEquals(2, GeneralLogic.parseOrder(s8)[1]);
    }
}
