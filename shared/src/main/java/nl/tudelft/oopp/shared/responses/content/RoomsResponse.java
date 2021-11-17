package nl.tudelft.oopp.shared.responses.content;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomsResponse {
    /**
     * List of all rooms in the response.
     */
    @NonNull
    List<RoomResponse> rooms = new ArrayList<>();
}
