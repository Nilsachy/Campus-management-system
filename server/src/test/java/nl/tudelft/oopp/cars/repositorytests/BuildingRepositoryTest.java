package nl.tudelft.oopp.cars.repositorytests;

import javax.persistence.EntityManager;

import nl.tudelft.oopp.cars.repositories.RoomRepository;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class BuildingRepositoryTest {

    @Autowired
    private RoomRepository repository;

    @Autowired
    private EntityManager entityManager;
}
