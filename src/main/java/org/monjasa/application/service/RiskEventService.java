package org.monjasa.application.service;

import com.vaadin.flow.data.provider.DataProvider;
import org.monjasa.application.model.risk.RiskEvent;
import org.monjasa.application.model.risk.RiskType;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.vaadin.flow.data.provider.SortDirection.ASCENDING;

public interface RiskEventService {

    default DataProvider<RiskEvent, Void> getDataProviderFromCallbacks() {
        return DataProvider.fromCallbacks(
                query -> {
                    int offset = query.getOffset();
                    int limit = query.getLimit();
                    Optional<Sort> sort = query.getSortOrders().stream()
                            .map(queryOrder -> Sort.by(queryOrder.getDirection() == ASCENDING ? Direction.ASC : Direction.DESC, queryOrder.getSorted()))
                            .findFirst();

                    List<RiskEvent> riskEvents = sort.isPresent() ? findAll(sort.get()) : findAll();
                    return riskEvents.stream();
                }, query -> Math.toIntExact(countAll())
        );
    }

    List<RiskEvent> findAll();

    List<RiskEvent> findAll(Sort sort);

    List<RiskEvent> findByRiskType(RiskType riskType);

    List<RiskEvent> findAssessed();

    List<RiskEvent> findAssessed(Sort sort);

    List<RiskEvent> findAssessedByRiskType(RiskType riskType);

    long countAll();

    long countByPredicate(Predicate<? super RiskEvent> predicate);

    RiskEvent save(RiskEvent riskEvent);
}
