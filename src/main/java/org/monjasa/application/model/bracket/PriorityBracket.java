package org.monjasa.application.model.bracket;

import lombok.RequiredArgsConstructor;
import org.monjasa.application.model.risk.RiskEvent;

import java.math.BigDecimal;
import java.util.DoubleSummaryStatistics;
import java.util.List;

@RequiredArgsConstructor
public enum PriorityBracket {

    LOW("Низький"),
    MEDIUM("Середній"),
    HIGH("Високий");

    public final String name;

    public static PriorityBracket getBracket(RiskEvent riskEvent, List<RiskEvent> riskEvents) {

        BigDecimal budgetLoss = riskEvent.getBudgetLoss();
        DoubleSummaryStatistics statistics = riskEvents.stream()
                .map(RiskEvent::getBudgetLoss)
                .mapToDouble(BigDecimal::doubleValue)
                .summaryStatistics();

        double interval = (statistics.getMax() - statistics.getMin()) / 3;

        if (budgetLoss.compareTo(BigDecimal.valueOf(statistics.getMin() + interval)) < 0) return LOW;
        else if (budgetLoss.compareTo(BigDecimal.valueOf(statistics.getMin() + 2 * interval)) > 0) return HIGH;
        else return MEDIUM;
    }

    @Override
    public String toString() {
        return name;
    }
}
