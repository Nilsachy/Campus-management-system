package nl.tudelft.oopp.shared.requests.delete;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteBikeReservationRequest implements DeleteReservationRequest  {
    /**
     * The id of the bike reservation that is to be deleted.
     * Format: integer.
     */
    private int id;
}
