package org.monjasa.application.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class RiskEvent {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private Long id;

    private RiskType riskType;
    private String name;
    private boolean assessed;
    private BigDecimal budget;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "probability_evaluation_id")
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Evaluation> probabilityEvaluations;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "loss_evaluation_id")
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Evaluation> lossEvaluations;

    @ManyToOne()
    @JoinColumn(name = "arrangement_id")
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

    public double getWeightedRiskLoss() {

        int weightSum = lossEvaluations.stream()
                .map(Evaluation::getWeight)
                .reduce(Integer::sum)
                .orElse(1);

        return lossEvaluations.stream()
                .mapToDouble(Evaluation::getWeightedValue)
                .summaryStatistics()
                .getSum() / weightSum;
    }

    public BigDecimal getBudgetLoss() {
        return budget.multiply(BigDecimal.valueOf(getWeightedRiskLoss()));
    }
}
