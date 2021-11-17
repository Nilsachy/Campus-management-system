package nl.tudelft.oopp.shared.requests.create;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoomRequest {
    String faculty;
    int building;
    String room;
    int capacity;
    boolean whiteboard;
    boolean staffOnly;


    /**
     * A more natural way to check for if something has a whiteboard.
     * @return if it has a whiteboard
     */
    public boolean hasWhiteboard() {
        return whiteboard;
    }
}
