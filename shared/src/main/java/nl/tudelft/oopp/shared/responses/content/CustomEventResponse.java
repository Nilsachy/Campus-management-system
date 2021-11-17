package nl.tudelft.oopp.shared.responses.content;

import java.util.Calendar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomEventResponse implements ReservationResponse {
    private long id;
    private String user;
    private String title;
    private Calendar start;
    private Calendar end;
    private String address;
    private String description;
}
