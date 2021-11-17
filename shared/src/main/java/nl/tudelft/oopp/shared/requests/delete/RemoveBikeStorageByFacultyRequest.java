package nl.tudelft.oopp.shared.requests.delete;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoveBikeStorageByFacultyRequest {
    String faculty;
}
