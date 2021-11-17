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
public class BikeReservationResponse implements ReservationResponse  {

    /**
     * The ID of the bike reservation.
     * Format: integer.
     */
    private int id;

    /**
     * The email of the user that the bike reservation belongs to.
     * Format: String. initial.lastname@(student.)?tudelft.nl
     * Example: d.vandenbroek@tudelft.nl
     */
    @NonNull
    private String user;

    /**
     * Name of the faculty that the bike is picked up from
     * Format: String.
     */
    @NonNull
    private String fromFaculty;

    /**
     * Name of the faculty that the bike is dropped off to
     * Format: String.
     */
    @NonNull
    private String toFaculty;

    /**
     * The date and time at which the bike is taken from the fromFacility.
     * Format: Calendar.
     */
    @NonNull
    private Calendar pickup;

    /**
     * The date and time at which the bike is returned to the toFacility.
     * Format: Calendar.
     */
    @NonNull
    private Calendar dropOff;
}
