package nl.tudelft.oopp.cars.entities;

import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "bike_reservation")
public class BikeReservation implements Reservation {
    @Id
    @GeneratedValue
    private int id;

    @Column(name = "user")
    private String user;

    @Column(name = "from_faculty")
    private String fromFaculty;

    @Column(name = "to_faculty")
    private String toFaculty;

    @Column(name = "pickup")
    private Calendar pickup;

    @Column(name = "drop_off")
    private Calendar dropOff;

    /**
     * Standard constructor, only omits the id, because that is generated.
     * @param user the user trying to get the reservation
     * @param fromFaculty the faculty at which the reservation is desired
     * @param toFaculty the faculty at which the bike will be returned
     * @param pickup the date and time at which the pickup happens
     * @param dropOff the date and time at which the dropoff happens
     */
    public BikeReservation(String user, String fromFaculty, String toFaculty,
                           Calendar pickup, Calendar dropOff) {
        this.user = user;
        this.fromFaculty = fromFaculty;
        this.toFaculty = toFaculty;
        this.pickup = pickup;
        this.dropOff = dropOff;
    }
}
