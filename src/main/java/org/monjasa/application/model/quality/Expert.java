package org.monjasa.application.model.quality;

import com.vaadin.flow.component.charts.model.DataSeriesItem;
import lombok.*;
import org.monjasa.application.model.Evaluation;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Builder
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

    public List<DataSeriesItem> processEvaluationDataItems() {
        return Arrays.stream(AttributeType.values())
                .map(attribute -> new DataSeriesItem(getAngleForAttributeVector(attribute), normalizeEvaluationForAttribute(attribute)))
                .collect(Collectors.toList());
    }

    public double normalizeEvaluationForAttribute(AttributeType attributeType) {

        if (expertType == ExpertType.TOTAL) {
            return getEvaluationForAttribute(attributeType).getValue() / weight * 10;
        }

        return weight * getEvaluationForAttribute(attributeType).getWeightedValue() / 100;
    }

    public Evaluation getEvaluationForAttribute(AttributeType attributeType) {
        return attributeEvaluations.get(attributeType);
    }

    public double getAngleForAttributeSector(AttributeType attributeType) {

        double weightSum = attributeEvaluations.values().stream()
                .mapToDouble(Evaluation::getWeight)
                .sum();

        return 360.0 * attributeEvaluations.get(attributeType).getWeight() / weightSum;
    }

    public double getAngleForAttributeSectorStart(AttributeType attributeType) {
        AttributeType initialType = AttributeType.values()[0];
        return Arrays.stream(AttributeType.values())
                .filter(type -> type.ordinal() < attributeType.ordinal())
                .reduce(
                        -0.5 * getAngleForAttributeSector(initialType),
                        (sum, type) -> sum + getAngleForAttributeSector(type),
                        Double::sum
                );
    }

    public double getAngleForAttributeSectorEnd(AttributeType attributeType) {
        AttributeType initialType = AttributeType.values()[0];
        return Arrays.stream(AttributeType.values())
                .filter(type -> type.ordinal() <= attributeType.ordinal())
                .reduce(
                        getAngleForAttributeSectorStart(initialType),
                        (sum, type) -> sum + getAngleForAttributeSector(type),
                        Double::sum
                );
    }

    public double getAngleForAttributeVector(AttributeType attributeType) {
        return (getAngleForAttributeSectorStart(attributeType) + getAngleForAttributeSectorEnd(attributeType)) / 2;
    }

    public double getAreaForAttribute(AttributeType attributeType) {

        AttributeType followingAttribute = attributeType.ordinal() + 1 < AttributeType.values().length
                ? AttributeType.values()[attributeType.ordinal() + 1]
                : AttributeType.values()[0];

        double angle = Math.toRadians(getAngleForAttributeVector(attributeType));
        double followingAngle = Math.toRadians(getAngleForAttributeVector(followingAttribute));

        double factor = normalizeEvaluationForAttribute(attributeType);
        double followingFactor = normalizeEvaluationForAttribute(followingAttribute);

        double area = (Math.sin(angle) * factor) * (Math.cos(followingAngle) * followingFactor)
                - (Math.cos(angle) * factor) * (Math.sin(followingAngle) * followingFactor);

        return Math.abs(area) / 2;
    }
}
