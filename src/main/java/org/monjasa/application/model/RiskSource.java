package org.monjasa.application.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskSource {
    private RiskType riskType;
    private String name;
    private boolean assessed;
}
