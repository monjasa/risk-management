package org.monjasa.application.service;

import lombok.RequiredArgsConstructor;
import org.monjasa.application.model.Arrangement;
import org.monjasa.application.repository.ArrangementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("arrangementService")
@RequiredArgsConstructor
public class DefaultArrangementService implements ArrangementService {

    private final ArrangementRepository arrangementRepository;

    @Override
    public List<Arrangement> findAll() {
        return arrangementRepository.findAll();
    }

    @Override
    public Arrangement findByName(String name) {
        return arrangementRepository.findByName(name).stream()
                .findFirst()
                .orElseThrow();
    }
}
