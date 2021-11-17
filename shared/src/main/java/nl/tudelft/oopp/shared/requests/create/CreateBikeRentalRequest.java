package nl.tudelft.oopp.shared.requests.create;

import java.util.Calendar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBikeRentalRequest {
    /**
     * Username of the user that is renting the bike.
     * Format: String: (insert id here)@(student.)?tudelft.nl
     * Example1: employeeid@tudelft.nl
     * Example2: studentid@student.tudelft.nl
     */
    private @NonNull String user;
    /**
     * Faculty ID of the faculty that the bike will be rented from
     * Format: String. Acronym
     */
    private @NonNull String fromFaculty;
    /**
     * Faculty ID of the faculty that the bike will be returned to
     * Format: String. Acronym
     */
    private @NonNull String toFaculty;
    /**
     * Date and time the bike will be rented from.
     * Format: Calendar.
     */
    private @NonNull Calendar fromDate;
    /**
     * Date and time the bike will be returned.
     * Format. Calendar.
     */
    private @NonNull Calendar toDate;
}
