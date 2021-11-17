package nl.tudelft.oopp.shared.responses.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuildingResponse {

    /**
     * The number of the building that is returned.
     */
    private int id;

    /**
     * The name of the building that is returned.
     */
    @NonNull
    private String name;

    /**
     * The address of where the building is located.
     */
    @NonNull
    private String address;

    /**
     * The name of the faculty that the building belongs to.
     */
    @NonNull
    private String faculty;
}