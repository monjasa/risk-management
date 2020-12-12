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
public class RiskSource {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private Long id;

    private RiskType riskType;
    private String name;
    private boolean assessed;
}
