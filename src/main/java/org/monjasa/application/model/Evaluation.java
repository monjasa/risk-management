package org.monjasa.application.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Evaluation {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private Long id;

    private double value;
    private double weight;

    public double getWeightedValue() {
        return weight * value;
    }
}
