package nl.tudelft.oopp.shared.responses.content;

import java.util.Calendar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementResponse {

    /**
     * ID of the Announcement.
     * Format: long
     */
    private long id;

    /**
     * The date and time at which the announcement was posted.
     * Format: Calendar
     */
    @NonNull
    private Calendar posted;

    /**
     * The date and time until which the announcement is relevant.
     * Format: Calendar
     */
    @NonNull
    private Calendar relevantUntil;

    /**
     * The title of the announcement.
     * Format: String
     */
    @NonNull
    private String title;

    /**
     * The information displayed in the announcement.
     * Format: String
     */
    @NonNull
    private String content;

    /**
     * The email of the user that posted the announcement.
     * Format: String. initial.lastname@(student.)?tudelft.nl
     * Example: d.vandenbroek@tudelft.nl
     */
    @NonNull
    private String user;


}
