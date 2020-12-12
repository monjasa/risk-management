package org.monjasa.application.service;

import org.monjasa.application.model.RiskEvent;
import org.monjasa.application.model.RiskType;

import java.util.List;
import java.util.function.Predicate;

public interface RiskEventService {

    List<RiskEvent> findAll();

    List<RiskEvent> findByRiskType(RiskType riskType);

    List<RiskEvent> findAssessed();

    List<RiskEvent> findAssessedByRiskType(RiskType riskType);

    long countAll();

    long countByPredicate(Predicate<? super RiskEvent> predicate);

    RiskEvent save(RiskEvent riskEvent);
}
