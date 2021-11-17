package nl.tudelft.oopp.shared.requests.read;

import java.util.Calendar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAvailableBikeStoragesRequest {
    /**
     * The date and time the bikeStorage must have bikes available.
     * Format: Calendar.
     */
    private @NonNull Calendar fromDate;
    /**
     * The date and time the bikeStorage must have space for bikes available.
     * Format: Calendar.
     */
    private @NonNull Calendar toDate;

}
