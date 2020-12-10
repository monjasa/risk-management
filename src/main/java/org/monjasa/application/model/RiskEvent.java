package org.monjasa.application.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.monjasa.application.model.bracket.ProbabilityBracket;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskEvent {

    private RiskType riskType;
    private String name;
    private boolean assessed;
    private BigDecimal budget;
    private List<Evaluation> probabilityEvaluations;
    private List<Evaluation> lossEvaluations;
    private Arrangement arrangement;

    public double getWeightedRiskProbability() {

        int weightSum = probabilityEvaluations.stream()
                .map(Evaluation::getWeight)
                .reduce(Integer::sum)
                .orElse(1);

        return probabilityEvaluations.stream()
                .mapToDouble(Evaluation::getWeightedValue)
                .summaryStatistics()
                .getSum() / weightSum;
    }
}
