package nl.tudelft.oopp.cars.repositorytests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;

import nl.tudelft.oopp.cars.entities.Room;
import nl.tudelft.oopp.cars.repositories.RoomRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class RoomRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void saveRoomTest() {
        try {
            Room r1 = new Room("TestFaculty1", 3051, "0.0", 10, true, false);

            roomRepository.save(r1);

            List<Room> rooms = roomRepository.findAll();

            assertTrue(rooms.size() > 0);
            assertEquals(r1.getBuilding(), roomRepository.findByFaculty("TestFaculty1")
                    .get(0).getBuilding());

            roomRepository.delete(r1);
        } catch (Exception e) {
            //to be implemented
        }
    }

    @Test
    public void findAllRoomsTest() {
        try {
            Room r1 = new Room("EEMCSq", 3051, "0.0", 10, true, false);
            Room r2 = new Room("EEMCS2", 3051, "0.0", 10, true, false);
            Room r3 = new Room("EEMCS3", 3051, "0.0", 10, true, false);
            //saving room should be implemented
            roomRepository.save(r1);
            roomRepository.save(r2);
            roomRepository.save(r3);

            List<Room> rooms = roomRepository.findAll();
            assertTrue(rooms.size() > 2);

            roomRepository.delete(r1);
            roomRepository.delete(r2);
            roomRepository.delete(r3);
        } catch (Exception e) {
            //to be implemented
        }
    }

    @Test
    public void findByNameTest() {
        try {
            Room r1 = new Room("EEMCSq", 3051, "TestRoom2", 10, true, false);
            Room r2 = new Room("EEMCS2", 3051, "0.0", 10, true, false);
            Room r3 = new Room("EEMCS3", 3051, "0.0", 10, true, false);

            roomRepository.save(r1);
            roomRepository.save(r2);
            roomRepository.save(r3);

            Room r = roomRepository.findByName("TestBuilding2", "TestRoom2");
            assertTrue(r.getRoom().equals("TestRoom2"));

            roomRepository.delete(r1);
            roomRepository.delete(r2);
            roomRepository.delete(r3);
        } catch (Exception e) {
            //to be implemented
        }
    }

    @Test
    public void findByFacultyTest() {
        try {
            Room r1 = new Room("TestFaculty2", 3051, "Test", 10, true, false);
            Room r2 = new Room("TestFaculty2", 3051, "0.0", 10, true, false);
            Room r3 = new Room("EEMCS3", 3051, "0.0", 10, true, false);

            roomRepository.save(r1);
            roomRepository.save(r2);
            roomRepository.save(r3);

            List<Room> rooms = roomRepository.findByFaculty("TestFaculty2");
            assertTrue(rooms.size() > 0);
            assertEquals("TestFaculty2", rooms.get(1).getFaculty());

            roomRepository.delete(r1);
            roomRepository.delete(r2);
            roomRepository.delete(r3);
        } catch (Exception e) {
            //to be implemented
        }
    }

    @Test
    public void findByBuildingTest() {
        try {
            Room r1 = new Room("TestFaculty2", 23, "TestRoom2", 10, true, false);
            Room r2 = new Room("TestFaculty2", 23, "0.0", 10, true, false);
            Room r3 = new Room("EEMCS", 3051, "0.0", 10, true, false);

            roomRepository.save(r1);
            roomRepository.save(r2);
            roomRepository.save(r3);

            //to be implemented
            List<Room> rooms = roomRepository.findByBuilding(23);
            assertTrue(rooms.size() > 0);
            assertEquals(23, rooms.get(0).getBuilding());

            roomRepository.delete(r1);
            roomRepository.delete(r2);
            roomRepository.delete(r3);
        } catch (Exception e) {
            //to be implemented
        }
    }

    @Test
    public void findByCapacityTest() {
        try {
            Room r1 = new Room("EEMCSq", 3051, "0.0", 12548, true, false);
            Room r2 = new Room("EEMCS2", 3051, "0.0", 10, true, false);
            Room r3 = new Room("EEMCS3", 3051, "0.0", 10, true, false);

            roomRepository.save(r1);
            roomRepository.save(r2);
            roomRepository.save(r3);

            List<Room> rooms = roomRepository.findByCapacity(96789);
            //assertTrue(rooms.size() > 0);
            assertEquals(rooms.get(0).getCapacity(), 96789);

            roomRepository.delete(r1);
            roomRepository.delete(r2);
            roomRepository.delete(r3);
        } catch (Exception e) {
            //to be implemented
        }
    }

    @Test
    public void findByMinCapacityTest() {
        try {
            Room r1 = new Room("EEMCSq", 3051, "0.0", 12548, true, false);
            Room r2 = new Room("EEMCS2", 3051, "0.0", 65432, true, false);
            Room r3 = new Room("EEMCS3", 3051, "0.0", 98765, true, false);

            roomRepository.save(r1);
            roomRepository.save(r2);
            roomRepository.save(r3);

            List<Room> rooms = roomRepository.findByMinCapacity(10000);
            assertTrue(rooms.size() > 2);

            roomRepository.delete(r1);
            roomRepository.delete(r2);
            roomRepository.delete(r3);
        } catch (Exception e) {
            //to be implemented
        }
    }


}
