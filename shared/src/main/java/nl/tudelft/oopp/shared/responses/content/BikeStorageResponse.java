package nl.tudelft.oopp.shared.responses.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BikeStorageResponse {

    /**
     * The name of the faculty that the bike storage is located in.
     * Format: String.
     */
    @NonNull
    private String faculty;

    /**
     * The capacity of the bike storage.
     * Format: integer.
     */
    private int capacity;
}
