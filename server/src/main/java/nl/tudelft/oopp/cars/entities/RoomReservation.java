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
@Table(name = "room_reservation")
public class RoomReservation implements Reservation {
    @Id
    @GeneratedValue
    private int id;

    @Column(name = "date")
    private Calendar date;

    @Column(name = "timeslot")
    private String timeslot;

    @Column(name = "user")
    private String user;

    @Column(name = "faculty")
    private String faculty;

    @Column(name = "building")
    private String building;

    @Column(name = "room")
    private String room;

    /**
     * Default constructor for a room reservation.
     * @param date the date at which the room is reserved
     * @param timeslot the timeslot in which it is taken
     * @param user the person renting the room
     * @param building the building in which the room is
     * @param room the name of the room
     */
    public RoomReservation(Calendar date, String timeslot, String user,
                           String faculty, String building, String room) {
        this.date = date;
        this.timeslot = timeslot;
        this.user = user;
        this.faculty = faculty;
        this.building = building;
        this.room = room;
    }
}
