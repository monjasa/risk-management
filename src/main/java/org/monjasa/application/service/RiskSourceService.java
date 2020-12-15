package org.monjasa.application.service;

import com.vaadin.flow.data.provider.DataProvider;
import org.monjasa.application.model.RiskSource;
import org.monjasa.application.model.RiskType;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.vaadin.flow.data.provider.SortDirection.ASCENDING;

public interface RiskSourceService {

    default DataProvider<RiskSource, Void> getDataProviderFromCallbacks() {
        return DataProvider.fromCallbacks(
                query -> {
                    int offset = query.getOffset();
                    int limit = query.getLimit();
                    Optional<Sort> sort = query.getSortOrders().stream()
                            .map(queryOrder -> Sort.by(queryOrder.getDirection() == ASCENDING ? Direction.ASC : Direction.DESC, queryOrder.getSorted()))
                            .findFirst();

                    List<RiskSource> riskSources = sort.isPresent() ? findAll(sort.get()) : findAll();
                    return riskSources.stream();
                }, query -> Math.toIntExact(countAll())
        );
    }

    List<RiskSource> findAll();

    List<RiskSource> findAll(Sort sort);

    List<RiskSource> findAssessedByRiskType(RiskType riskType);

    long countAll();

    long countByPredicate(Predicate<? super RiskSource> predicate);

    RiskSource save(RiskSource riskSource);
}

