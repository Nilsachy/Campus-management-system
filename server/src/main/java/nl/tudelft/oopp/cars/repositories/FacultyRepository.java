package nl.tudelft.oopp.cars.repositories;

import java.util.List;

import nl.tudelft.oopp.cars.entities.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface FacultyRepository extends JpaRepository<Faculty, String> {

    @Query(value = "SELECT * FROM faculty f WHERE f.name = :name", nativeQuery = true)
    public List<Faculty> findByName(@Param("name") String name);
}
