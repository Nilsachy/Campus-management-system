package nl.tudelft.oopp.cars.repositories;

import java.util.List;

import nl.tudelft.oopp.cars.entities.CustomEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomEventRepository extends JpaRepository<CustomEvent, Long> {
    @Query(value = "SELECT * FROM custom_event c WHERE c.user = :user", nativeQuery = true)
    List<CustomEvent> findByUser(@Param("user") String user);
}
