package nl.tudelft.oopp.shared.requests.read;

import java.util.Calendar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAvailableRoomsRequest {
    Calendar calendar;
    String time;
    int building;
}
