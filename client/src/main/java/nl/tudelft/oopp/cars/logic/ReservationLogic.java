package nl.tudelft.oopp.cars.logic;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import nl.tudelft.oopp.shared.requests.delete.DeleteBikeReservationRequest;
import nl.tudelft.oopp.shared.requests.delete.DeleteReservationRequest;
import nl.tudelft.oopp.shared.requests.delete.DeleteRoomReservationRequest;
import nl.tudelft.oopp.shared.requests.delete.RemoveEventByIdRequest;
import nl.tudelft.oopp.shared.responses.content.BikeReservationResponse;
import nl.tudelft.oopp.shared.responses.content.CustomEventResponse;
import nl.tudelft.oopp.shared.responses.content.ReservationResponse;
import nl.tudelft.oopp.shared.responses.content.RoomReservationResponse;

public class ReservationLogic {

    /**
     * Parses the ReservationsResponse into an observable list of user-friendly strings.
     * @param list The observable list to add the strings into
     * @param reservations The list of ReservationsResponse to get the information from
     */
    public static void parseResponse(ObservableList<String> list,
                                     List<ReservationResponse> reservations) {
        for (ReservationResponse response : reservations) {
            if (response == null) {
                continue;
            }
            String responseString;
            if (response instanceof RoomReservationResponse) {
                RoomReservationResponse r = (RoomReservationResponse) response;
                responseString = String.format("Room Reservation of %s on %td/%tm/%tY from %s "
                        + "to %s in %s %s", r.getRoom(), r.getDate(), r.getDate(), r.getDate(),
                        r.getTimeslot().split("-")[0], r.getTimeslot().split("-")[1],
                        r.getBuilding(), "(" + r.getFaculty() + ")");
            } else if (response instanceof BikeReservationResponse) {
                BikeReservationResponse b = (BikeReservationResponse) response;
                responseString = String.format("Bike Rental, pick-up at %s on %td/%tm/%tY %tR and "
                                + "drop-off at %s on %td/%tm/%tY %tR", b.getFromFaculty(),
                        b.getPickup(), b.getPickup(), b.getPickup(), b.getPickup().getTime(),
                        b.getToFaculty(), b.getDropOff(), b.getDropOff(), b.getDropOff(),
                        b.getDropOff().getTime());
            } else if (response instanceof CustomEventResponse) {
                CustomEventResponse c = (CustomEventResponse) response;
                responseString = String.format("%s at %s, starting on %td/%tm/%tY %tR and "
                        + "ending on %td/%tm/%tY %tR:\n%s", c.getTitle(), c.getAddress(),
                        c.getStart(), c.getStart(), c.getStart(), c.getStart(), c.getEnd(),
                        c.getEnd(), c.getEnd(), c.getEnd(), c.getDescription());
            } else {
                responseString = "ERROR";
            }
            list.add(responseString);
        }
    }

    /**
     * Parses the given string into the object needed to remove the reservation.
     * @param reservation the string containing information in a specific format
     * @param request the response object without any attributes set
     * @throws ParseException called when parsing is not possible for some value
     */
    public static void removeReservation(List<ReservationResponse> all, String reservation,
                                         Object request) throws ParseException {
        if (request instanceof DeleteRoomReservationRequest) {
            RoomReservationResponse response = new RoomReservationResponse();
            parseRoomReservation(reservation, response);
            long id = searchListForId(all, response);
            DeleteRoomReservationRequest room = (DeleteRoomReservationRequest) request;
            room.setId((int) id);
        } else if (request instanceof DeleteBikeReservationRequest) {
            BikeReservationResponse response = new BikeReservationResponse();
            parseBikeReservation(reservation, response);
            long id = searchListForId(all, response);
            DeleteBikeReservationRequest bike = (DeleteBikeReservationRequest) request;
            bike.setId((int) id);
        } else {
            CustomEventResponse response = new CustomEventResponse();
            parseCustomEvent(reservation, response);
            long id = searchListForId(all, response);
            RemoveEventByIdRequest custom = (RemoveEventByIdRequest) request;
            custom.setCustomEventId(id);
        }
    }

    /**
     * Parses the given string back into a RoomReservationResponse object.
     * @param reservation a string containing all information
     * @param response the empty RoomReservationResponse to set the attributes of
     */
    public static void parseRoomReservation(String reservation, RoomReservationResponse response)
            throws ParseException {
        // "Room Reservation of %s on %td/%tm/%tY from %s to %s in %s %s"

        // "%s on %td/%tm/%tY from %s to %s in %s %s"
        String[] s1 = reservation.split("Room Reservation of ");
        // "%s", "%td/%tm/%tY from %s to %s in %s %s"
        String[] s2 = s1[1].split(" on ", 2);
        response.setRoom(s2[0]);
        // "%td/%tm/%tY", "%s to %s in %s %s"
        String[] s3 = s2[1].split(" from ", 2);
        response.setDate(GeneralLogic.parseDateTime("Date", s3[0]));
        // "%s", "%s in %s %s"
        String[] s4 = s3[1].split(" to ", 2);
        // "%s", "%s %s"
        String[] s5 = s4[1].split(" in ", 2);
        response.setTimeslot(s4[0] + "-" + s5[0]);
        // "%s, "%s"
        String[] s6 = s5[1].split(" \\(", 2);
        response.setBuilding(s6[0]);
        response.setFaculty(s6[1].split("\\)", 2)[0]);

        System.out.println(response);
    }

    /**
     * Parses the given string back into a BikeReservationResponse object.
     * @param reservation a string containing all information
     * @param response the empty BikeReservationResponse to set the attributes of
     */
    public static void parseBikeReservation(String reservation, BikeReservationResponse response)
            throws ParseException {
        // "Bike Rental, pick-up at %s on %td/%tm/%tY %tR and drop-off at %s on %td/%tm/%tY %tR"

        // "%s on %td/%tm/%tY %tR and drop-off at %s on %td/%tm/%tY %tR"
        String[] s1 = reservation.split("Bike Rental, pick-up at ", 2);
        // "%s", "%td/%tm/%tY %tR and drop-off at %s on %td/%tm/%tY %tR"
        String[] s2 = s1[1].split(" on ", 2);
        response.setFromFaculty(s2[0]);
        // "%td/%tm/%tY %tR", "%s on %td/%tm/%tY %tR"
        String[] s3 = s2[1].split(" and drop-off at ", 2);
        response.setPickup(GeneralLogic.parseDateTime("", s3[0]));
        // "%s", "%td/%tm/%tY %tR"
        String[] s4 = s3[1].split(" on ", 2);
        response.setToFaculty(s4[0]);
        response.setDropOff(GeneralLogic.parseDateTime("", s4[1]));
    }

    /**
     * Parses the given string back into a CustomEventResponse object.
     * @param reservation a string containing all information
     * @param response the empty CustomEventResponse to set the attributes of
     */
    public static void parseCustomEvent(String reservation, CustomEventResponse response)
            throws ParseException {
        // "%s at %s, starting on %td/%tm/%tY %tR and ending on %td/%tm/%tY %tR:\n%s"

        // "%s", "%s, starting on %td/%tm/%tY %tR and ending on %td/%tm/%tY %tR:\n%s"
        String[] s1 = reservation.split(" at ", 2);
        response.setTitle(s1[0]);
        // "%s", "%td/%tm/%tY %tR and ending on %td/%tm/%tY %tR:\n%s"
        String[] s2 = s1[1].split(", starting on ", 2);
        response.setAddress(s2[0]);
        // "%td/%tm/%tY %tR", "%td/%tm/%tY %tR:\n%s"
        String[] s3 = s2[1].split(" and ending on ", 2);
        response.setStart(GeneralLogic.parseDateTime("", s3[0]));
        // "%td/%tm/%tY %tR", "%s"
        String[] s4 = s3[1].split(":\n", 2);
        response.setEnd(GeneralLogic.parseDateTime("", s4[0]));
        response.setDescription(s4[1]);
    }

    /**
     * Searches through the list of ReservationResponses to find the given ReservationResponse.
     * @param all the list containing all ReservationResponses
     * @param response the ReservationResponse to find in the list
     * @return the id of the ReservationResponse
     */
    public static long searchListForId(List<ReservationResponse> all,
                                       ReservationResponse response) {
        long id = -1;
        if (response instanceof RoomReservationResponse) {
            RoomReservationResponse r = (RoomReservationResponse) response;
            id = searchListForRoomId(id, all, r);
        } else if (response instanceof BikeReservationResponse) {
            BikeReservationResponse b = (BikeReservationResponse) response;
            id = searchListForBikeId(id, all, b);
        } else {
            CustomEventResponse c = (CustomEventResponse) response;
            id = searchListForCustomId(id, all, c);
        }
        return id;
    }

    /**
     * Searches through the list of ReservationResponses to find the given RoomReservationResponse.
     * @param id standard value -1
     * @param all the list containing all ReservationResponses
     * @param r the RoomReservationResponse to find in the list
     * @return the id of the RoomReservationResponse
     */
    public static long searchListForRoomId(long id, List<ReservationResponse> all,
                                           RoomReservationResponse r) {
        for (ReservationResponse reservation : all) {
            if (reservation instanceof RoomReservationResponse) {
                RoomReservationResponse res = (RoomReservationResponse) reservation;
                if (r.getRoom().equals(res.getRoom()) && r.getBuilding().equals(res.getBuilding())
                        && r.getFaculty().equals(res.getFaculty())
                        && r.getTimeslot().equals(res.getTimeslot())
                        && GeneralLogic.compareCalendars("Date", r.getDate(), res.getDate())) {
                    id =  res.getId();
                    break;
                }
            }
        }
        return id;
    }

    /**
     * Searches through the list of ReservationResponses to find the given BikeReservationResponse.
     * @param id standard value -1
     * @param all the list containing all ReservationResponses
     * @param b the BikeReservationResponse to find in the list
     * @return the id of the BikeReservationResponse
     */
    public static long searchListForBikeId(long id, List<ReservationResponse> all,
                                           BikeReservationResponse b) {
        for (ReservationResponse reservation : all) {
            if (reservation instanceof BikeReservationResponse) {
                BikeReservationResponse res = (BikeReservationResponse) reservation;
                if (b.getFromFaculty().equals(res.getFromFaculty())
                        && b.getToFaculty().equals(res.getToFaculty())
                        && GeneralLogic.compareCalendars("", b.getPickup(), res.getPickup())
                        && GeneralLogic.compareCalendars("", b.getDropOff(), res.getDropOff())) {
                    id =  res.getId();
                    break;
                }
            }
        }
        return id;
    }

    /**
     * Searches through the list of ReservationResponses to find the given CustomEvent.
     * @param id standard value -1
     * @param all the list containing all ReservationResponses
     * @param c the CustomEvent to find in the list
     * @return the id of the CustomEvent
     */
    public static long searchListForCustomId(long id, List<ReservationResponse> all,
                                             CustomEventResponse c) {
        for (ReservationResponse reservation : all) {
            if (reservation instanceof CustomEventResponse) {
                CustomEventResponse res = (CustomEventResponse) reservation;
                if (c.getTitle().equals(res.getTitle()) && c.getAddress().equals(res.getAddress())
                        && GeneralLogic.compareCalendars("", c.getStart(), res.getStart())
                        && GeneralLogic.compareCalendars("", c.getEnd(), res.getEnd())
                        && c.getDescription().equals(res.getDescription())) {
                    id =  res.getId();
                    break;
                }
            }
        }
        return id;
    }

    /**
     * Logic for sorting rooms.
     * @param reservations list of reservations
     * @param std 1: sort by time, 2: sort by faculty 3: type
     * @param opt 1: ascending 2: descending
     * @return list of all rooms that are open at the given time and date in the given faculty.
     */
    public static List<ReservationResponse> sortingReservationOption(
            List<ReservationResponse> reservations, int std, int opt) {

        List<ReservationResponse> sortedRes;

        if (std == 1) {
            sortedRes = ReservationLogic.sortByName(reservations, opt);
        } else if (std == 2) {
            sortedRes = ReservationLogic.sortByTime(reservations, opt);
        } else {
            return reservations;
        }
        return sortedRes;
    }

    /**
     * Logic for sorting rooms depending on their name.
     * @param reservations list of rooms
     * @param option 1: ascending 2: descending
     * @return sorted list of all rooms that are available.
     */
    public static List<ReservationResponse>
        sortByName(List<ReservationResponse> reservations, int option) {
        int i = -1;
        if (option == 2) {
            i = 1;
        }
        int finalI = i;
        reservations.sort((r1, r2) -> {
            String f1 = "";
            String f2 = "";

            if (r1 instanceof RoomReservationResponse) {
                f1 = ((RoomReservationResponse) r1).getRoom();
            } else if (r1 instanceof BikeReservationResponse) {
                f1 = ((BikeReservationResponse) r1).getFromFaculty();
            } else {
                f1 = ((CustomEventResponse) r1).getTitle();
            }
            if (r2 instanceof RoomReservationResponse) {
                f2 = ((RoomReservationResponse) r2).getRoom();
            } else if (r2 instanceof BikeReservationResponse) {
                f2 = ((BikeReservationResponse) r2).getFromFaculty();
            } else {
                f2 = ((CustomEventResponse) r2).getTitle();
            }

            if (f1.compareTo(f2) > 0) {
                return -1 * finalI;
            } else if (f1.compareTo(f2) < 0) {
                return finalI;
            }
            return 0;
        });
        return reservations;
    }

    /**
     * Logic for sorting rooms depending on their date and time.
     * @param reservations list of rooms
     * @param option 1: ascending 2: descending
     * @return sorted list of all rooms that are available.
     */
    public static List<ReservationResponse>
        sortByTime(List<ReservationResponse> reservations, int option) {
        int i = 1;
        if (option == 2) {
            i = -1;
        }
        int finalI = i;
        reservations.sort((r1, r2) -> {
            Calendar d1 = Calendar.getInstance();
            Calendar d2 = Calendar.getInstance();
            String t1 = "";
            String t2 = "";
            if (r1 instanceof RoomReservationResponse) {
                d1 = ((RoomReservationResponse) r1).getDate();
                t1 = ((RoomReservationResponse) r1).getTimeslot().split("-")[0];
            } else if (r1 instanceof BikeReservationResponse) {
                d1 = ((BikeReservationResponse) r1).getPickup();
            } else {
                d1 = ((CustomEventResponse) r1).getStart();
            }
            if (r2 instanceof RoomReservationResponse) {
                d2 = ((RoomReservationResponse) r2).getDate();
                t2 = ((RoomReservationResponse) r2).getTimeslot().split("-")[0];
            } else if (r2 instanceof BikeReservationResponse) {
                d2 = ((BikeReservationResponse) r2).getPickup();
            } else {
                d2 = ((CustomEventResponse) r2).getStart();
            }

            if (d1.after(d2)) {
                return finalI;
            } else if (d1.before(d2)) {
                return -1 * finalI;
            } else if (t1.compareTo(t2) > 0) {
                return finalI;
            } else if (t1.compareTo(t2) < 0) {
                return -1 * finalI;
            }
            return 0;
        });

        return reservations;
    }

    /**
     * Fills the listView with the reservations from the list.
     * @param reservations the list of ReservationResponses that contains all reservations
     * @param ordering the choiceBox containing all the information needed for sorting the list
     * @param list the list that holds the parsed response
     * @param listView the listView that needs to show all elements in the list
     */
    public static void initializeEvents(List<ReservationResponse> reservations,
                                        ChoiceBox<String> ordering,
                                        ObservableList<String> list, ListView<String> listView) {
        list.clear();
        int[] evOrder = GeneralLogic.parseOrder(ordering
                .getSelectionModel().getSelectedItem());
        if (reservations != null) {
            reservations = ReservationLogic.sortingReservationOption(reservations,
                    evOrder[0], evOrder[1]);
            ReservationLogic.parseResponse(list, reservations);
        }
        listView.setItems(list);
    }
}
