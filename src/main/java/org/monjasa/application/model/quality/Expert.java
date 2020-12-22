package org.monjasa.application.model.quality;

import lombok.*;
import org.monjasa.application.model.Evaluation;

import javax.persistence.*;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Expert {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private Long id;

    @Enumerated(EnumType.STRING)
    private ExpertType expertType;

    private int weight;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinTable(name = "expert_evaluation_mapping",
            joinColumns = { @JoinColumn(name = "expert_id", referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "evaluation_id", referencedColumnName = "id") })
    @MapKeyEnumerated(value = EnumType.STRING)
    private Map<AttributeType, Evaluation> attributeEvaluations;

    public Evaluation getEvaluationForAttribute(AttributeType attributeType) {
        return attributeEvaluations.get(attributeType);
    }
}
