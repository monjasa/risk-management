package org.monjasa.application.repository;

import org.monjasa.application.model.risk.RiskSource;
import org.monjasa.application.model.risk.RiskType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiskSourceRepository extends JpaRepository<RiskSource,Long> {
    List<RiskSource> findByRiskType(RiskType riskType);
    List<RiskSource> findByAssessed(boolean assessed);
    List<RiskSource> findByAssessedAndRiskType(boolean assessed, RiskType riskType);
}
