package nl.tudelft.oopp.cars.repositories;

import java.util.List;

import nl.tudelft.oopp.cars.entities.RoomReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoomReservationRepository extends JpaRepository<RoomReservation, Integer> {

    /*
    To do:
    - get by...
      - id
      - date
      - faculty
      - building
      - timeslot
      - date + timeslot

    - add new? check if this works
    - delete by id
    - update by id
     */

    @Query(value = "SELECT * FROM room_reservation r WHERE r.user = :user", nativeQuery = true)
    List<RoomReservation> getByUser(String user);

    @Query(value = "SELECT * FROM room_reservation r", nativeQuery = true)
    List<RoomReservation> getAllReservation();
}
