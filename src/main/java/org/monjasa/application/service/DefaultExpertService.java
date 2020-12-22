package org.monjasa.application.service;

import lombok.RequiredArgsConstructor;
import org.monjasa.application.model.quality.Expert;
import org.monjasa.application.model.quality.ExpertType;
import org.monjasa.application.repository.ExpertRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

@Service("expertService")
@RequiredArgsConstructor
public class DefaultExpertService implements ExpertService {

    private final ExpertRepository expertRepository;

    @Override
    public List<Expert> findAll() {
        return expertRepository.findAll();
    }

    @Override
    public List<Expert> findAll(Sort sort) {
        return expertRepository.findAll(sort);
    }

    @Override
    public Expert findByExpertType(ExpertType expertType) {
        return expertRepository.findByExpertType(expertType).stream()
                .findFirst()
                .orElseThrow();
    }

    @Override
    public long countAll() {
        return expertRepository.count();
    }

    @Override
    public Expert save(Expert expert) {
        return expertRepository.save(expert);
    }
}
