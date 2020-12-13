package org.monjasa.application.service;

import lombok.RequiredArgsConstructor;
import org.monjasa.application.model.RiskSource;
import org.monjasa.application.model.RiskType;
import org.monjasa.application.repository.RiskSourceRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

@Service("riskSourceService")
@RequiredArgsConstructor
public class DefaultRiskSourceService implements RiskSourceService {

    private final RiskSourceRepository riskSourceRepository;

    @Override
    public List<RiskSource> findAll() {
        return riskSourceRepository.findAll(Sort.by(Direction.DESC, "name"));
    }

    @Override
    public List<RiskSource> findAll(Sort sort) {
        return riskSourceRepository.findAll(sort);
    }

    @Override
    public long countAll() {
        return riskSourceRepository.count();
    }

    @Override
    public List<RiskSource> findAssessedByRiskType(RiskType riskType) {
        return riskSourceRepository.findByAssessedAndRiskType(true, riskType);
    }

    @Override
    public long countByPredicate(Predicate<? super RiskSource> predicate) {
        return riskSourceRepository.findAll().stream()
                .filter(predicate)
                .count();
    }

    @Override
    public RiskSource save(RiskSource riskSource) {
        return riskSourceRepository.save(riskSource);
    }
}
