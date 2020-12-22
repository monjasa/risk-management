package org.monjasa.application.model.risk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class RiskSource {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private Long id;

    @Enumerated(EnumType.STRING)
    private RiskType riskType;

    private String name;
    private boolean assessed;
}
