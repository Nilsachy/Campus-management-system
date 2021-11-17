package nl.tudelft.oopp.cars.repositories;

import java.util.List;

import nl.tudelft.oopp.cars.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, String> {

    @Query(value = "SELECT * FROM user u WHERE u.role = ?1", nativeQuery = true)
    List<User> getByRole(String role);

    //    @Query(value = "UPDATE user SET password = ?2 WHERE email = ?1", nativeQuery = true)
    //    void updatePasswordByEmail(String email, int passwordHash);

    //    @Query(value = "SELECT * FROM user u", nativeQuery = true)
    //    List<User> getAllUser();

    //    @Query(value = "SELECT * FROM user u WHERE u.email = ?1", nativeQuery = true)
    //    List<User> getUserByEmail(String email);

    //    @Query(value = "INSERT INTO user (email, password, role) VALUES (?1, ?2, ?3)")
    //    void insertUser(String email, int password, String role);
}
