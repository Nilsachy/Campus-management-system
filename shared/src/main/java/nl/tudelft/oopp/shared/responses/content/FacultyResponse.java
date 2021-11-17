package nl.tudelft.oopp.shared.responses.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacultyResponse {
    /**
     * The id of the faculty.
     * Format: String. Generally the acronym of the faculty name.
     */
    private @NonNull String id;
    /**
     * The name of the faculty.
     * Format: String.
     */
    private @NonNull String name;
    /**
     * The faculty's phone number.
     * Format: String: +31 (0)15 27 898[insert two digits here]
     */
    private @NonNull String phone;
}
