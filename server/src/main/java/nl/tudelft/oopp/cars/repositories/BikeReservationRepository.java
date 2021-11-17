package nl.tudelft.oopp.cars.repositories;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

import nl.tudelft.oopp.cars.entities.BikeReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BikeReservationRepository extends JpaRepository<BikeReservation, Integer> {

    @Query(value = "SELECT * FROM bike_reservation r WHERE r.user = :user", nativeQuery = true)
    List<BikeReservation> getByUser(String user);

    @Query(value = "SELECT * FROM bike_reservation WHERE from_faculty = ?1", nativeQuery = true)
    List<BikeReservation> getByFromFaculty(String faculty);

    @Query(value = "SELECT * FROM bike_reservation WHERE to_faculty = ?1", nativeQuery = true)
    List<BikeReservation> getByToFaculty(String faculty);

    @Query(value = "SELECT * FROM bike_reservation WHERE pickup = ?1", nativeQuery = true)
    List<BikeReservation> getByPickupDate(Date date);

    @Query(value = "SELECT * FROM bike_reservation WHERE pickup = ?1 AND pickup_time = ?2",
            nativeQuery = true)
    List<BikeReservation> getByPickup(Date date, Time time);

    @Query(value = "SELECT * FROM bike_reservation",
            nativeQuery = true)
    List<BikeReservation> findAllFromBikeReservation();
}
