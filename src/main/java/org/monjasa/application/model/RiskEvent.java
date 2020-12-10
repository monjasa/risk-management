package org.monjasa.application.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private List<Evaluation> evaluation;
    private Arrangement arrangement;
}
