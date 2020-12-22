package org.monjasa.application.model.quality;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ExpertType {

    INDUSTRIAL("Експерт галузі"),
    USABILITY("Експерт зручності користування"),
    PROGRAMMING("Експерт з програмування"),
    USERS("Потенційні користувачі");

    @JsonValue
    public final String name;

    @Override
    public String toString() {
        return name;
    }
}