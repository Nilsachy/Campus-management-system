package nl.tudelft.oopp.cars.repositories;

import java.util.List;

import nl.tudelft.oopp.cars.entities.BikeStorage;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BikeStorageRepository extends JpaRepository<BikeStorage, String> {

    /**
     * Returns all faculties with a certain minimum capacity in their bike storage.
     * @param min minimum total bike capacity
     * @return List of faculties whose bikes storage is at least min
     */
    @Query(value = "SELECT * FROM bike_storage WHERE max_available >= :min", nativeQuery = true)
    List<BikeStorage> findByMinAvailable(@Param("min") int min);

    //    @Modifying
    //    @Query(value = "INSERT INTO bike_storage (faculty, max_available) VALUES (:fac, :cap)"
    //    , nativeQuery = true)
    //    @Transactional
    //    void addNewBikeStorage(@Param("fac") String faculty, @Param("cap") int capacity);

    @Query(value = "SELECT * FROM bike_storage WHERE faculty = ?1 ",
            nativeQuery = true)
    List<BikeStorage> findByFaculty(String faculty);

    @Query(value = "SELECT * FROM bike_storage ORDER BY faculty",
            nativeQuery = true)
    List<BikeStorage> findAll();

}
