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
public class BuildingsResponse {
    /**
     * A list of all the buildings within the response.
     */
    @NonNull
    List<BuildingResponse> buildings = new ArrayList<>();
}
