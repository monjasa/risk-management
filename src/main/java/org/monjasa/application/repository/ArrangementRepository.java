package org.monjasa.application.repository;

import org.monjasa.application.model.Arrangement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArrangementRepository extends JpaRepository<Arrangement, Long> {
    List<Arrangement> findByName(String name);
}
