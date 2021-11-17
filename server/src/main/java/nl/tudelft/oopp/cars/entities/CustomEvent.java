package nl.tudelft.oopp.cars.entities;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "custom_event")
public class CustomEvent {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "user")
    private String user;

    @Column(name = "title")
    private String title;

    @Column(name = "start")
    private Calendar start;

    @Column(name = "end")
    private Calendar end;

    @Column(name = "address", nullable = true)
    private String address;

    @Column(name = "description", nullable = true)
    private String description;

    /**
     * Constructor - creates instance of CustomEvent.
     * @param user - user making whose event it is
     * @param title - title of the event
     * @param start - starting date and time of the event
     * @param end - ending date and time of the event
     * @param address - address of the event. Is optional.
     * @param description - description of the event. Is optional.
     */
    public CustomEvent(String user, String title, Calendar start, Calendar end, String address,
                       String description) {
        this.user = user;
        this.title = title;
        this.start = start;
        this.end = end;
        this.address = address;
        this.description = description;
    }
}
