package nl.tudelft.oopp.shared.requests.create;

import java.util.Calendar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAnnouncementRequest {
    private Calendar posted;
    private Calendar relevantUntil;
    private String title;
    private String content;
    private String user;
}
