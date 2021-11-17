package nl.tudelft.oopp.shared.requests.create;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateFacultyRequest {
    public String id;
    public String name;
    public String phoneNumber;
}
