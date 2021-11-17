package nl.tudelft.oopp.cars.repositories;

import java.util.List;
import java.util.Optional;

import nl.tudelft.oopp.cars.entities.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BuildingRepository extends JpaRepository<Building, Integer> {

    /**
     * Query for finding buildings by an id.
     *
     * @param id id of the building
     * @return the building with that id
     */
    @Query(value = "SELECT * FROM building b WHERE b.id = ?1", nativeQuery = true)
    public Optional<Building> findById(int id);

    /**
     * Query for finding buildings by an address.
     *
     * @param address address of the building
     * @return the building with that address
     */
    @Query(value = "SELECT * FROM building b WHERE b.address = ?1", nativeQuery = true)
    public List<Building> findByAddress(String address);

    /**
     * Method that executes a query to return the building with a particular name.
     *
     * @param name name of the building
     * @return building entity
     */
    @Query(value = "SELECT * FROM building b WHERE b.name = ?1", nativeQuery = true)
    public List<Building> findByName(String name);

    /**
     * Method to execute the query to find all buildings of a certain faculty when they have one.
     *
     * @param faculty faculty of which you want the buildings listed
     * @return List of buildings associated to that faculty
     */
    @Query(value = "SELECT * FROM building b WHERE b.faculty = ?1", nativeQuery = true)
    public List<Building> findByFaculty(String faculty);

    //    /**
    //     * Method to return all buildings not associated to a faculty.
    //     *
    //     * @return List of buildings not associated to a faculty
    //     */
    //    // should probably be faculty general
    //    @Query(value = "SELECT * FROM building b WHERE b.faculty IS NULL", nativeQuery = true)
    //    public List<Building> findFacultyNull();
}
