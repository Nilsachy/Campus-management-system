package nl.tudelft.oopp.shared.responses.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomResponse {

    /**
     * The ID of the room that is returned.
     * Format: integer.
     */
    private long id;

    /**
     * The name of the room that is returned.
     * Format: String.
     */
    @NonNull
    private String name;

    /**
     * The number of the building that the room is located in.
     * Format: integer.
     */
    private int building;

    /**
     * The capacity of the room that is returned.
     * Format: integer.
     */
    private int capacity;

    /**
     * An attribute that shows whether or not the room has a whiteboard.
     * Format: Boolean.
     */
    private boolean hasWhiteboard;

    /**
     * An attribute that shows whether or not the room is for staff only.
     * Format: Boolean.
     */
    private boolean staffOnly;
}
