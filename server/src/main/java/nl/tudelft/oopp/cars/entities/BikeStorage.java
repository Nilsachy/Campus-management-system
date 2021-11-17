package nl.tudelft.oopp.cars.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bike_storage")
public class BikeStorage {
    @Id
    private String faculty;

    @Column(name = "max_available")
    private int maxAvailable;
}