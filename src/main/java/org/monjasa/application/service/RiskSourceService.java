package org.monjasa.application.service;

import org.monjasa.application.model.RiskSource;
import org.monjasa.application.model.RiskType;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.function.Predicate;

public interface RiskSourceService {

    List<RiskSource> findAll();

    List<RiskSource> findAll(Sort sort);

    List<RiskSource> findAssessedByRiskType(RiskType riskType);

    long countAll();

    long countByPredicate(Predicate<? super RiskSource> predicate);

    RiskSource save(RiskSource riskSource);
}

