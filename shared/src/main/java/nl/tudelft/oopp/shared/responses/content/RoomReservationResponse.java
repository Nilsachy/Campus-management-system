package nl.tudelft.oopp.shared.responses.content;

import java.util.Calendar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RoomReservationResponse implements ReservationResponse {

    /**
     * The ID of the room reservation.
     * Format: integer.
     */
    private int id;

    /**
     * The date on which the room is reserved.
     * Format: Calendar.
     */
    private Calendar date;

    /**
     * The timeslot during which the room is reserved.
     * Format: String.
     */
    private String timeslot;

    /**
     * The email of the user that the room reservation belongs to.
     * Format: String. initial.lastname@(student.)?tudelft.nl
     * Example: d.vandenbroek@tudelft.nl
     */
    private String user;

    /**
     * The name of the faculty that the building belongs to.
     * Format: String.
     */
    private String faculty;

    /**
     * The name of the building that the reserved room is located in.
     * Format: String.
     */
    private String building;

    /**
     * The name of the room corresponding to the reservation.
     * Format: String.
     */
    private String room;

}
