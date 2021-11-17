package nl.tudelft.oopp.shared.requests.create;

import java.util.Calendar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoomReservationRequest {
    Calendar date;
    String timeSlot;
    String user;
    String faculty;
    String building;
    String room;
}
