package org.monjasa.application.service;

import lombok.RequiredArgsConstructor;
import org.monjasa.application.model.RiskEvent;
import org.monjasa.application.model.RiskType;
import org.monjasa.application.repository.RiskEventRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service("riskEventService")
@RequiredArgsConstructor
public class DefaultRiskEventService implements RiskEventService {

    private final RiskEventRepository riskEventRepository;

    @Override
    public List<RiskEvent> findAll() {
        return riskEventRepository.findAll();
    }

    @Override
    public List<RiskEvent> findAll(Sort sort) {
        return riskEventRepository.findAll(sort);
    }

    @Override
    public List<RiskEvent> findByRiskType(RiskType riskType) {
        return riskEventRepository.findByRiskType(riskType);
    }

    @Override
    public List<RiskEvent> findAssessed() {
        return riskEventRepository.findByAssessed(true);
    }

    @Override
    public List<RiskEvent> findAssessed(Sort sort) {
        return riskEventRepository.findAll(sort).stream()
                .filter(RiskEvent::isAssessed)
                .collect(Collectors.toList());
    }

    @Override
    public List<RiskEvent> findAssessedByRiskType(RiskType riskType) {
        return riskEventRepository.findByAssessedAndRiskType(true, riskType);
    }

    @Override
    public long countAll() {
        return riskEventRepository.count();
    }

    @Override
    public long countByPredicate(Predicate<? super RiskEvent> predicate) {
        return riskEventRepository.findAll().stream()
                .filter(predicate)
                .count();
    }

    @Override
    public RiskEvent save(RiskEvent riskEvent) {
        return riskEventRepository.save(riskEvent);
    }
}
