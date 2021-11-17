package nl.tudelft.oopp.cars.repositories;

import java.util.List;
import javax.transaction.Transactional;

import nl.tudelft.oopp.cars.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomRepository extends JpaRepository<Room, Long> {
    /*
    To do:
    - update by id
     */

    //    /**
    //     * Inserts a new tuple of a room into the database.
    //     * @param faculty faculty of the room
    //     * @param building building that the room is in
    //     * @param room name of the room
    //     * @param capacity number of people the room is fit for
    //     * @param whiteboard whether or not the room has a whiteboard
    //     * @param staffOnly whether reservations of this room can only be made by staff members
    //     */
    //    @Modifying
    //    @Query(value =
    //            "INSERT INTO room
    //                    (id, faculty, building, room, capacity, whiteboard, staff_only) " +
    //                    "VALUES (null, :faculty, :building, :room, :capacity, :whiteboard, :SO)"
    //            , nativeQuery = true)
    //    @Transactional
    //    void saveRoom(@Param("faculty") String faculty, @Param("building") int building,
    //                  @Param("room") String room, @Param("capacity") int capacity,
    //                  @Param("whiteboard") boolean whiteboard,
    //                  @Param("SO") boolean staffOnly);

    //    /**
    //     * Removes room by its id
    //     * @param id long representation of the ID of the room to be removed
    //     */
    //    @Modifying
    //    @Query(value = "DELETE FROM room WHERE id = :id"
    //            , nativeQuery = true)
    //    @Transactional
    //    void deleteRoom(@Param("id") long id);

    /**
     * Updates a given attribute of a tuple with a String value to a new specified value.
     * @param attribute attribute to be updated. Can be faculty or room.
     * @param value new value of the attribute of the tuple
     * @param id id of the room tuple to be updated
     */
    @Modifying
    @Query(value = "UPDATE room SET :attribute = :val WHERE id = :id", nativeQuery = true)
    @Transactional
    void updateStringAt(@Param("id") long id, @Param("attribute") String attribute,
                        @Param("val") String value);

    /**
     * Updates the value of a given integer attribute of a tuple in the Room table with
     * the specified value.
     * @param id id of the room whose value will be updated
     * @param attribute the integer attribute to be updated. This can include building,
     *                  capacity, whiteboard* and staff_only*. *can only take value of 0 or 1.
     * @param value new value of given attribute.
     */
    @Modifying
    @Query(value = "UPDATE room SET :attribute = :val WHERE id = :id", nativeQuery = true)
    @Transactional
    void updateIntAt(@Param("id") long id, @Param("attribute") String attribute,
                     @Param("val") int value);

    //    /**
    //     * Executes query to find all rooms in the DB.
    //     * @return List of rooms stored in DB
    //     */
    //    @Query(value = "SELECT * FROM room r", nativeQuery = true)
    //    List<Room> findAllRooms();

    /**
     * Executes query to find room by building and name.
     * @param name name of the room
     * @param building name of the building
     * @return room with the particular name and in that building
     */
    @Query(value = "SELECT * FROM room r WHERE r.room = ?1 AND r.building = ?2", nativeQuery = true)
    Room findByName(String name, String building);

    /**
     * Executes query to find rooms in DB of a certain faculty.
     * @param faculty faculty that is associated with the room
     * @return list of rooms associated to the faculty
     */
    @Query(value = "SELECT * FROM room r WHERE r.faculty = ?1", nativeQuery = true)
    List<Room> findByFaculty(String faculty);

    /**
     * Executes query to find all rooms within a particular building.
     * @param buildingNumber name of the building the room is in
     * @return List of rooms within that building
     */
    @Query(value = "SELECT * FROM room r WHERE r.building = ?1", nativeQuery = true)
    List<Room> findByBuilding(int buildingNumber);

    /**
     * Executes query to find all rooms with a specific capacity.
     * @param capacity capacity of the room
     * @return List of rooms with that capacity
     */
    @Query(value = "SELECT * FROM room r WHERE r.capacity = ?1", nativeQuery = true)
    List<Room> findByCapacity(int capacity);

    /**
     * Executes query to find all rooms with a certain minimum capacity.
     * @param capacity minimum capacity of the room (includes number)
     * @return List of rooms with at least that capacity
     */
    @Query(value = "SELECT * FROM room r WHERE r.capacity >= ?1", nativeQuery = true)
    List<Room> findByMinCapacity(int capacity);

    /**
     * Executes query to find all rooms with a whiteboard.
     * @return List of rooms with a whiteboard
     */
    @Query(value = "SELECT * FROM room r WHERE r.whiteboard = true", nativeQuery = true)
    List<Room> findByWhiteboard();

    /**
     * Executes query to find all rooms only reservable by staff.
     * @return List of rooms that are reservable by staff only
     */
    @Query(value = "SELECT * FROM room r WHERE r.staff_only = true", nativeQuery = true)
    List<Room> findByStaffOnly();

    /**
     * Executes query to find all rooms that can be reserved by anyone.
     * @return List of rooms that can be reserved bu anyone
     */
    @Query(value = "SELECT * FROM room r WHERE r.staff_only = false", nativeQuery = true)
    List<Room> findByNotStaffOnly();

    // /**
    // * executes query to find all rooms that can be reserved by anyone
    //  * @return List of rooms that can be reserved bu anyone
    //  */
    //  @Query(value = "SELECT * FROM Room r WHERE r.faculty = ?1 AND r.building = ?2
    //      AND r.capacity >= ?3 AND
    // r.whiteboard = true AND r.staff_only = ?5 ", nativeQuery = true)
    // List<Room> findAllOpt(String faculty, String building, int capacity, boolean whiteBoard,
    //      boolean staff);

}
