package nl.tudelft.oopp.shared.requests.create;

import java.util.Calendar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCustomEventRequest {
    String user;
    String title;
    Calendar start;
    Calendar end;
    String address;
    String description;

    //    public CreateCustomEventRequest(Calendar instance,
    //    Calendar start,
    //    Calendar end,String address,
    //    String title, String content, String user) {
    //    }
}