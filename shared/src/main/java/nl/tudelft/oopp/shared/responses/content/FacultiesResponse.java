package nl.tudelft.oopp.shared.responses.content;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacultiesResponse {
    /**
     * A list of faculties.
     * Format: List of FacultyResponse objects.
     */
    private @NonNull List<FacultyResponse> faculties = new ArrayList<>();
}
