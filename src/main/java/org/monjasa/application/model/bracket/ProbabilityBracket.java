package org.monjasa.application.model.bracket;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum ProbabilityBracket {

    VERY_LOW("Дуже низька", 0.00, 0.10),
    LOW("Низька",0.10, 0.25),
    MEDIUM("Середня",0.25, 0.50),
    HIGH("Висока",0.50, 0.75),
    VERY_HIGH("Дуже висока",0.75, 1.00);

    public final String name;
    public final double lowerBound;
    public final double upperBound;

    public static ProbabilityBracket getBracket(double probability) {

        if (probability < 0) throw new RuntimeException("Probability must be non-negative floating point value.");

        return Arrays.stream(ProbabilityBracket.values())
                .filter(bracket -> probability >= bracket.lowerBound && probability < bracket.upperBound)
                .findAny()
                .orElse(VERY_HIGH);
    }


    @Override
    public String toString() {
        return name;
    }
}
