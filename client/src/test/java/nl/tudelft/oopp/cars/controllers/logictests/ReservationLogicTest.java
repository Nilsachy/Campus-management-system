package nl.tudelft.oopp.cars.controllers.logictests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.tudelft.oopp.cars.logic.GeneralLogic;
import nl.tudelft.oopp.cars.logic.ReservationLogic;
import nl.tudelft.oopp.shared.responses.content.BikeReservationResponse;
import nl.tudelft.oopp.shared.responses.content.CustomEventResponse;
import nl.tudelft.oopp.shared.responses.content.ReservationResponse;
import nl.tudelft.oopp.shared.responses.content.RoomReservationResponse;
import org.junit.jupiter.api.Test;

public class ReservationLogicTest {

    Calendar c0 = new GregorianCalendar(2020, Calendar.MARCH, 24, 9, 0);
    Calendar c1 = new GregorianCalendar(2020, Calendar.MARCH, 28, 10, 0);
    Calendar c2 = new GregorianCalendar(2020, Calendar.APRIL, 2, 11, 0);
    RoomReservationResponse r0 = new RoomReservationResponse(1, c0, "11:00-13:00", "me",
            "EEMCS", "EWI", "Boole");
    RoomReservationResponse r1 = new RoomReservationResponse(3, c1, "13:00-15:00", "me",
            "All", "Pulse", "Boole");
    BikeReservationResponse b0 = new BikeReservationResponse(2, "me",
            "EWI", "General", c0, c1);
    BikeReservationResponse b1 = new BikeReservationResponse(4, "me",
            "General", "EEMCS", c1, c2);
    CustomEventResponse ce0 = new CustomEventResponse(5, "me", "Appointment", c0, c1,
            "SomeStreet 999, 0303AD", "Content goes here.");
    CustomEventResponse ce1 = new CustomEventResponse(6, "me", "Doctor", c1, c2,
            "AnotherStreet 111, 3030DA", "Some more content here");

    ObservableList<String> ob = FXCollections.observableArrayList();

    @Test
    public void parseResponseTest() {
        List<ReservationResponse> list = new ArrayList<>();
        list.add(r0);
        list.add(b0);
        list.add(ce0);
        ReservationLogic.parseResponse(ob, list);
        assertEquals("[Room Reservation of Boole on 24/03/2020 from 11:00 to 13:00 in EWI (EEMCS), "
                + "Bike Rental, pick-up at EWI on 24/03/2020 09:00 and drop-off at General "
                + "on 28/03/2020 10:00, Appointment at SomeStreet 999, 0303AD, starting on "
                + "24/03/2020 09:00 and ending on 28/03/2020 10:00:\nContent goes here.]",
                ob.toString());
    }

    @Test
    public void parseRoomReservationTest() throws ParseException {
        String s = "Room Reservation of Boole on 24/03/2020 from 11:00 to 13:00 in EWI (EEMCS)";
        RoomReservationResponse res = new RoomReservationResponse();
        ReservationLogic.parseRoomReservation(s, res);
        assertEquals(r0.getRoom(), res.getRoom());
        assertEquals(r0.getBuilding(), res.getBuilding());
        assertEquals(r0.getFaculty(), res.getFaculty());
        assertTrue(GeneralLogic.compareCalendars("Date", r0.getDate(), res.getDate()));
        assertEquals(r0.getTimeslot(), res.getTimeslot());
    }

    @Test
    public void parseBikeReservationTest() throws ParseException {
        String s = "Bike Rental, pick-up at EWI on 24/03/2020 09:00 and drop-off at General "
                + "on 28/03/2020 10:00";
        BikeReservationResponse res = new BikeReservationResponse();
        ReservationLogic.parseBikeReservation(s, res);
        assertEquals(b0.getFromFaculty(), res.getFromFaculty());
        assertEquals(b0.getToFaculty(), res.getToFaculty());
        assertTrue(GeneralLogic.compareCalendars("", b0.getPickup(), res.getPickup()));
        assertTrue(GeneralLogic.compareCalendars("", b0.getDropOff(), res.getDropOff()));
    }

    @Test
    public void parseCustomEventTest() throws ParseException {
        String s = "Appointment at SomeStreet 999, 0303AD, starting on 24/03/2020 09:00 "
                + "and ending on 28/03/2020 10:00:\nContent goes here.";
        CustomEventResponse res = new CustomEventResponse();
        ReservationLogic.parseCustomEvent(s, res);
        assertEquals(ce0.getTitle(), res.getTitle());
        assertEquals(ce0.getAddress(), res.getAddress());
        assertEquals(ce0.getDescription(), res.getDescription());
        assertTrue(GeneralLogic.compareCalendars("", ce0.getStart(), res.getStart()));
        assertTrue(GeneralLogic.compareCalendars("", ce0.getEnd(), res.getEnd()));
    }

    @Test
    public void searchListForIdTest() {
        List<ReservationResponse> list = new ArrayList<>();
        list.add(r0);
        list.add(r1);
        list.add(b0);
        list.add(b1);
        list.add(ce0);
        list.add(ce1);
        assertEquals(1, ReservationLogic.searchListForId(list, r0));
        assertEquals(2, ReservationLogic.searchListForId(list, b0));
        assertEquals(3, ReservationLogic.searchListForId(list, r1));
        assertEquals(4, ReservationLogic.searchListForId(list, b1));
        assertEquals(5, ReservationLogic.searchListForId(list, ce0));
        assertEquals(6, ReservationLogic.searchListForId(list, ce1));
    }

    @Test
    public void sortByNameTest() {
        List<ReservationResponse> reservations = new ArrayList<>();
        reservations.add(r0);
        reservations.add(r1);
        reservations.add(b0);
        reservations.add(b1);

        List<ReservationResponse> resultAscending = new ArrayList<>();
        resultAscending.add(r0);
        resultAscending.add(r1);
        resultAscending.add(b0);
        resultAscending.add(b1);

        List<ReservationResponse> resultDescending = new ArrayList<>();
        resultDescending.add(b1);
        resultDescending.add(b0);
        resultDescending.add(r0);
        resultDescending.add(r1);

        ReservationLogic.sortByName(reservations, 1);
        assertEquals(reservations.get(0), resultAscending.get(0));
        assertEquals(reservations.get(1), resultAscending.get(1));
        assertEquals(reservations.get(2), resultAscending.get(2));
        assertEquals(reservations.get(3), resultAscending.get(3));

        ReservationLogic.sortByName(reservations, 2);
        assertEquals(reservations.get(0), resultDescending.get(0));
        assertEquals(reservations.get(1), resultDescending.get(1));
        assertEquals(reservations.get(2), resultDescending.get(2));
        assertEquals(reservations.get(3), resultDescending.get(3));
    }

    @Test
    public void sortByTimeTest() {
        List<ReservationResponse> reservations = new ArrayList<ReservationResponse>();
        reservations.add(r0);
        reservations.add(r1);
        reservations.add(b0);
        reservations.add(b1);

        List<ReservationResponse> resultAscending = new ArrayList<ReservationResponse>();
        resultAscending.add(b0);
        resultAscending.add(r0);
        resultAscending.add(b1);
        resultAscending.add(r1);
        
        List<ReservationResponse> resultDescending = new ArrayList<ReservationResponse>();
        resultDescending.add(r1);
        resultDescending.add(b1);
        resultDescending.add(r0);
        resultDescending.add(b0);

        ReservationLogic.sortByTime(reservations, 1);
        assertEquals(reservations.get(0), resultAscending.get(0));
        assertEquals(reservations.get(1), resultAscending.get(1));
        assertEquals(reservations.get(2), resultAscending.get(2));
        assertEquals(reservations.get(3), resultAscending.get(3));

        ReservationLogic.sortByTime(reservations, 2);
        assertEquals(reservations.get(0), resultDescending.get(0));
        assertEquals(reservations.get(1), resultDescending.get(1));
        assertEquals(reservations.get(2), resultDescending.get(2));
        assertEquals(reservations.get(3), resultDescending.get(3));
    }
}