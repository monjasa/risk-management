package org.monjasa.application.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evaluation {

    private double value;
    private int weight;

    public double getWeightedValue() {
        return weight * value;
    }
}
