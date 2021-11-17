package nl.tudelft.oopp.shared.requests.delete;

import java.util.Calendar;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteRoomReservationRequest implements DeleteReservationRequest {
    private int id;
}