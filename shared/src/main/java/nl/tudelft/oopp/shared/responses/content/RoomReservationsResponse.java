package nl.tudelft.oopp.shared.responses.content;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomReservationsResponse {
    List<RoomReservationResponse> roomReservationsResponses =
            new ArrayList<RoomReservationResponse>();
}
