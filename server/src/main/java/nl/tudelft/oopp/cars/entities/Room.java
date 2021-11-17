package nl.tudelft.oopp.cars.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "faculty")
    private String faculty;

    @Column(name = "building")
    private int building;

    @Column(name = "room")
    private String room;

    @Column(name = "capacity")
    private int capacity;

    @Column(name = "whiteboard")
    private boolean whiteboard;

    @Column(name = "staff_only")
    private boolean staffOnly;

    /**
     * Full args constructor for room.
     * @param faculty The faculty to which the room belongs
     * @param building The building in which the room is
     * @param room Name of the room
     * @param capacity The capacity of the room
     * @param whiteboard If the room has a whiteboard
     * @param staffOnly If the room is staff only
     */
    public Room(String faculty, int building, String room, int capacity,
                boolean whiteboard, boolean staffOnly) {
        this.faculty = faculty;
        this.building = building;
        this.room = room;
        this.capacity = capacity;
        this.whiteboard = whiteboard;
        this.staffOnly = staffOnly;
    }

    /**
     * Just a little overload for the getter to make it more natural.
     * @return if the room has a whiteboard
     */
    public boolean hasWhiteboard() {
        return whiteboard;
    }
}
