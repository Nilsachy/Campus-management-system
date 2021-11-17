package nl.tudelft.oopp.shared.requests.read;

import java.util.Calendar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetByDateRequest {
    public Calendar date;
}
