package org.monjasa.application.views.pages;

import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.monjasa.application.model.RiskEvent;
import org.monjasa.application.model.bracket.ProbabilityBracket;
import org.monjasa.application.service.RiskEventService;
import org.monjasa.application.views.MainView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import javax.annotation.PostConstruct;
import java.util.*;

import static com.vaadin.flow.data.provider.SortDirection.ASCENDING;

@Route(value = "risk-event-probabilities", layout = MainView.class)
@PageTitle("Ймовірності настання ризикових подій")
public class RiskEventProbabilitiesView extends VerticalLayout {

    public static final int EVALUATIONS_COUNT = 5;

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired private RiskEventService riskEventService;

    private final GridPro<RiskEvent> riskEventsGrid;

    @PostConstruct
    public void initializeDataProvider() {
        riskEventsGrid.setDataProvider(DataProvider.fromCallbacks(
                query -> {
                    Optional<Sort> sort = query.getSortOrders().stream()
                            .map(queryOrder -> Sort.by(queryOrder.getDirection() == ASCENDING ? Direction.ASC : Direction.DESC, queryOrder.getSorted()))
                            .findFirst();

                    int offset = query.getOffset();
                    int limit = query.getLimit();

                    List<RiskEvent> riskEvents = sort.isPresent() ? riskEventService.findAssessed(sort.get()) : riskEventService.findAssessed();
                    return riskEvents.stream();
                }, query -> Math.toIntExact(riskEventService.countByPredicate(RiskEvent::isAssessed)))
        );
    }

    public RiskEventProbabilitiesView() {

        setId("risk-event-probabilities-view");

        riskEventsGrid = new GridPro<>();
        riskEventsGrid.addThemeName("row-stripes");
        riskEventsGrid.addThemeName("wrap-cell-content");
        riskEventsGrid.getStyle().set("margin-bottom", "2em");
        riskEventsGrid.setHeight("750px");


        riskEventsGrid.addColumn(RiskEvent::getRiskType)
                .setSortProperty("riskType")
                .setFlexGrow(10)
                .setHeader("Тип ризиків");
        riskEventsGrid.addColumn(RiskEvent::getName)
                .setSortProperty("name")
                .setFlexGrow(100)
                .setHeader("Назва події");

        List<Column<RiskEvent>> evaluationColumns = new ArrayList<>(EVALUATIONS_COUNT);
        List<Column<RiskEvent>> weightedEvaluationColumns = new ArrayList<>(EVALUATIONS_COUNT);

        for (int i = 0; i < EVALUATIONS_COUNT; i++) {
            final int index = i;
            Column<RiskEvent> column = riskEventsGrid.addEditColumn(
                    riskEvent -> riskEvent.getProbabilityEvaluations().get(index).getValue(),
                    new NumberRenderer<>(riskEvent -> riskEvent.getProbabilityEvaluations().get(index).getValue(), "%(.2f", Locale.US)
            )
                    .text((item, newValue) -> item.getProbabilityEvaluations().get(index).setValue(Double.parseDouble(newValue)))
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setHeader(String.format("Експерт %d", index + 1));

            evaluationColumns.add(column);
        }

        for (int i = 0; i < EVALUATIONS_COUNT; i++) {
            final int index = i;
            Column<RiskEvent> column = riskEventsGrid.addColumn(new NumberRenderer<>(riskEvent -> riskEvent.getProbabilityEvaluations().get(index).getWeightedValue(), "%(.2f", Locale.US))
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setHeader(String.format("Експерт %d", index + 1));

            weightedEvaluationColumns.add(column);
        }

        HeaderRow headerRow = riskEventsGrid.prependHeaderRow();
        headerRow.join(evaluationColumns.toArray(Column[]::new)).setText("Оцінки експертів");
        headerRow.join(weightedEvaluationColumns.toArray(Column[]::new)).setText("Оцінки експертів з урахуванням вагомості");

        riskEventsGrid.addColumn(new NumberRenderer<>(RiskEvent::getWeightedRiskProbability, "%(.2f", Locale.US))
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(10)
                .setHeader("Зважена ймовірність");

        riskEventsGrid.addColumn(riskEvent -> ProbabilityBracket.getBracket(riskEvent.getWeightedRiskProbability()))
                .setClassNameGenerator(riskEvent -> ProbabilityBracket.getBracket(riskEvent.getWeightedRiskProbability()).name().toLowerCase())
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(10)
                .setHeader("Ймовірність виникнення");

        riskEventsGrid.addItemPropertyChangedListener(event -> {
            riskEventService.save(event.getItem());
        });

        add(new H1("Етап 2.1. Визначення ймовірності настання ризикових подій"));
        add(new H2("Класифікація настання ризикових подій"));
        add(riskEventsGrid);
    }
}
