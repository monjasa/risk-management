package org.monjasa.application.repository;

import org.monjasa.application.model.risk.RiskEvent;
import org.monjasa.application.model.risk.RiskType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiskEventRepository extends JpaRepository<RiskEvent, Long> {
    List<RiskEvent> findByRiskType(RiskType riskType);
    List<RiskEvent> findByAssessed(boolean assessed);
    List<RiskEvent> findByAssessedAndRiskType(boolean assessed, RiskType riskType);
}
