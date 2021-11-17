package nl.tudelft.oopp.shared.requests.update;

import java.util.Calendar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCustomEventRequest {
    /**
     * The ID of the event that needs to be changed.
     */
    private long id;

    /**
     * The email of the user who requests the event update.
     * So users cannot update each other's events.
     */
    @NonNull
    private String user;

    /**
     * The new title for the event.
     */
    @NonNull
    private String title;

    /**
     * The new address for the event.
     */
    @NonNull
    private String address;

    /**
     * The new description for the event.
     */
    @NonNull
    private String description;

    /**
     * The new start time for the event.
     */
    @NonNull
    private Calendar start;

    /**
     * The new end time for the event.
     */
    @NonNull
    private Calendar end;
}
