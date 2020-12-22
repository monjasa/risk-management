package org.monjasa.application.model.quality;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AttributeType {

    PRECISENESS("Точність управління та обчислень"),
    UI_CONSISTENCY("Ступінь стандартності інтерфейсів"),
    FUNCTIONAL_COMPLETENESS("Функціональна повнота"),
    FAULT_TOLERANCE("Стійкість до помилок"),
    EXTENSIBILITY("Можливість розширення"),
    CONVENIENCE("Зручність роботи"),
    SIMPLICITY("Простота роботи"),
    CONSISTENCY("Відповідність чинним стандартам"),
    PORTABILITY("Переносимість між ПЗ"),
    LEARNING_CAPABILITY("Зручність навчання");

    @JsonValue
    public final String name;

    @Override
    public String toString() {
        return name;
    }
}
