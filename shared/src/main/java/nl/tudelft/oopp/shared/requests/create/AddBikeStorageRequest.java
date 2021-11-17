package nl.tudelft.oopp.shared.requests.create;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddBikeStorageRequest {
    private String faculty;
    private int maxAvailable;
}
