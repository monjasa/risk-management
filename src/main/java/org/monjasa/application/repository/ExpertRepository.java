package org.monjasa.application.repository;

import org.monjasa.application.model.quality.Expert;
import org.monjasa.application.model.quality.ExpertType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpertRepository extends JpaRepository<Expert, Long> {
    List<Expert> findByExpertType(ExpertType expertType);
}
