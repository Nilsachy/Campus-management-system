package nl.tudelft.oopp.shared.requests.create;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddBuildingRequest {
    private int id;
    private String name;
    private String address;
    private String faculty;
}
