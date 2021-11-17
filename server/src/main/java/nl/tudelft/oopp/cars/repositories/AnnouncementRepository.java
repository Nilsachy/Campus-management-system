package nl.tudelft.oopp.cars.repositories;

import java.util.Calendar;
import java.util.List;

import javax.transaction.Transactional;

import nl.tudelft.oopp.cars.entities.Announcement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    @Query(value = "SELECT * FROM announcement a WHERE :date BETWEEN a.posted AND a.relevant_until",
            nativeQuery = true)
    List<Announcement> getByDate(@Param("date") Calendar date);

    @Transactional
    @Query(value = "DELETE FROM announcement a WHERE :date > a.relevant_until", nativeQuery = true)
    @Modifying
    void deleteNotRelevant(@Param("date") Calendar date);

    @Transactional
    @Query(value = "DELETE FROM announcement a WHERE a.id = :id", nativeQuery = true)
    @Modifying
    void removeById(@Param("id") long id);

}
