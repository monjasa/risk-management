package org.monjasa.application.service;

import com.vaadin.flow.data.provider.DataProvider;
import org.monjasa.application.model.quality.Expert;
import org.monjasa.application.model.quality.ExpertType;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.vaadin.flow.data.provider.SortDirection.ASCENDING;

public interface ExpertService {

    default DataProvider<Expert, Void> getDataProviderFromCallbacks() {
        return DataProvider.fromCallbacks(
                query -> {
                    int offset = query.getOffset();
                    int limit = query.getLimit();
                    Optional<Sort> sort = query.getSortOrders().stream()
                            .map(queryOrder -> Sort.by(queryOrder.getDirection() == ASCENDING ? Direction.ASC : Direction.DESC, queryOrder.getSorted()))
                            .findFirst();

                    List<Expert> experts = sort.isPresent() ? findAll(sort.get()) : findAll();
                    return experts.stream();
                }, query -> Math.toIntExact(countAll())
        );
    }

    List<Expert> findAll();

    List<Expert> findAll(Sort sort);

    Expert findByExpertType(ExpertType expertType);

    long countAll();

    Expert save(Expert expert);
}
