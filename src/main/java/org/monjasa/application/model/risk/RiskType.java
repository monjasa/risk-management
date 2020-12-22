package org.monjasa.application.model.risk;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RiskType {

    TECHNICAL("Технічні"),
    BUDGET("Вартісні"),
    SCHEDULE("Планові"),
    OPERATIONAL("Операційні");

    @JsonValue
    public final String name;

    @Override
    public String toString() {
        return name;
    }
}
