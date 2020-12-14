package org.monjasa.application.views.pages;

import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.monjasa.application.model.RiskEvent;
import org.monjasa.application.model.RiskType;
import org.monjasa.application.model.bracket.PriorityBracket;
import org.monjasa.application.model.bracket.ProbabilityBracket;
import org.monjasa.application.service.RiskEventService;
import org.monjasa.application.views.MainView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.vaadin.flow.data.provider.SortDirection.ASCENDING;

@Route(value = "risk-events-loss", layout = MainView.class)
@PageTitle("Величина ризику")
public class RiskEventLossView extends VerticalLayout {

    public static final int EVALUATIONS_COUNT = 5;

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    private RiskEventService riskEventService;

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

    public RiskEventLossView() {

        setId("risk-event-probabilities-view");

        riskEventsGrid = new GridPro<>();
        riskEventsGrid.addThemeName("row-stripes");
        riskEventsGrid.addThemeName("wrap-cell-content");
        riskEventsGrid.getStyle().set("margin-bottom", "2em");
        riskEventsGrid.setHeight("750px");

        riskEventsGrid.addEditColumn(RiskEvent::getRiskType)
                .select(RiskEvent::setRiskType, RiskType.class)
                .setSortProperty("riskType")
                .setHeader("Тип ризиків");
        riskEventsGrid.addEditColumn(RiskEvent::getName)
                .text(RiskEvent::setName)
                .setSortProperty("name")
                .setFlexGrow(10)
                .setHeader("Назва події");
        riskEventsGrid.addEditColumn(RiskEvent::getBudget, new NumberRenderer<>(RiskEvent::getBudget, "%(.2f тис. грн.", Locale.US))
                .text((item, newValue) -> item.setBudget(BigDecimal.valueOf(Double.parseDouble(newValue))))
                .setSortProperty("budget")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setHeader("Вартість реалізації");

        List<Column<RiskEvent>> evaluationColumns = new ArrayList<>(EVALUATIONS_COUNT);
        List<Column<RiskEvent>> weightedEvaluationColumns = new ArrayList<>(EVALUATIONS_COUNT);

        for (int i = 0; i < EVALUATIONS_COUNT; i++) {
            final int index = i;
            Column<RiskEvent> column = riskEventsGrid.addEditColumn(
                    riskEvent -> riskEvent.getLossEvaluations().get(index).getValue(),
                    new NumberRenderer<>(riskEvent -> riskEvent.getLossEvaluations().get(index).getValue(), "%(.2f", Locale.US)
            )
                    .text((item, newValue) -> item.getLossEvaluations().get(index).setValue(Double.parseDouble(newValue)))
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setAutoWidth(true)
                    .setHeader(String.format("Е-%d", index + 1));

            evaluationColumns.add(column);
        }

        for (int i = 0; i < EVALUATIONS_COUNT; i++) {
            final int index = i;
            Column<RiskEvent> column = riskEventsGrid.addColumn(new NumberRenderer<>(riskEvent -> riskEvent.getLossEvaluations().get(index).getWeightedValue(), "%(.2f", Locale.US))
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setAutoWidth(true)
                    .setHeader(String.format("Е-%d", index + 1));

            weightedEvaluationColumns.add(column);
        }

        HeaderRow headerRow = riskEventsGrid.prependHeaderRow();
        headerRow.join(evaluationColumns.toArray(Column[]::new)).setText("Оцінки експертів");
        headerRow.join(weightedEvaluationColumns.toArray(Column[]::new)).setText("Оцінки експертів з урахуванням їх вагомості");

        riskEventsGrid.addColumn(new NumberRenderer<>(RiskEvent::getWeightedRiskLoss, "%(.2f", Locale.US))
                .setTextAlign(ColumnTextAlign.CENTER)
                .setHeader("Можливі збитки");

        riskEventsGrid.addColumn(new NumberRenderer<>(RiskEvent::getBudgetLoss, "%(.2f тис. грн.", Locale.US))
                .setTextAlign(ColumnTextAlign.CENTER)
                .setHeader("Величина ризику");

        riskEventsGrid.addColumn(riskEvent -> PriorityBracket.getBracket(riskEvent, riskEventService.findAll()))
                .setClassNameGenerator(riskEvent -> PriorityBracket.getBracket(riskEvent, riskEventService.findAll()).name().toLowerCase())
                .setTextAlign(ColumnTextAlign.CENTER)
                .setHeader("Рівень пріоритету");

        riskEventsGrid.addItemPropertyChangedListener(event -> {
            riskEventService.save(event.getItem());
            riskEventsGrid.getDataProvider().refreshAll();
        });

        add(new H1("Етап 2.2. Визначення можливих збитків від ризику і визначення величини ризику"));
        add(riskEventsGrid);
    }
}
