package org.monjasa.application.views.pages;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.monjasa.application.model.RiskEvent;
import org.monjasa.application.model.bracket.ProbabilityBracket;
import org.monjasa.application.views.MainView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

@Route(value = "risk-event-probabilities", layout = MainView.class)
@PageTitle("Ймовірності настання ризикових подій")
public class RiskEventProbabilitiesView extends VerticalLayout {

    public static final int EVALUATIONS_COUNT = 5;

    public RiskEventProbabilitiesView() {

        setId("risk-event-probabilities-view");

        List<RiskEvent> riskEvents = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            riskEvents.addAll(objectMapper.readValue(
                    RiskEventProbabilitiesView.class.getClassLoader().getResourceAsStream("data/risk-events.json"),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, RiskEvent.class)
            ));
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        GridPro<RiskEvent> evaluationsGrid = new GridPro<>();
        evaluationsGrid.addThemeName("row-stripes");
        evaluationsGrid.addThemeName("wrap-cell-content");
        evaluationsGrid.setHeightByRows(true);

        evaluationsGrid.addColumn(RiskEvent::getRiskType, "riskType")
                .setHeader("Тип ризиків");

        GridPro<RiskEvent> riskEventsGrid = new GridPro<>();
        riskEventsGrid.addThemeName("row-stripes");
        riskEventsGrid.addThemeName("wrap-cell-content");

        ListDataProvider<RiskEvent> dataProvider = new ListDataProvider<>(riskEvents);
        dataProvider.setFilter(RiskEvent::isAssessed);
        riskEventsGrid.setDataProvider(dataProvider);

        riskEventsGrid.addColumn(RiskEvent::getRiskType, "riskType")
                .setFlexGrow(5)
                .setHeader("Тип ризиків");
        riskEventsGrid.addColumn(RiskEvent::getName, "name")
                .setFlexGrow(100)
                .setHeader("Назва події");

        List<Column<RiskEvent>> evaluationColumns = new ArrayList<>(EVALUATIONS_COUNT);
        for (int i = 0; i < EVALUATIONS_COUNT; i++) {
            final int index = i;

            Column<RiskEvent> column = riskEventsGrid.addEditColumn(riskEvent -> riskEvent.getProbabilityEvaluations().get(index).getValue())
                    .text((item, newValue) -> item.getProbabilityEvaluations().get(index).setValue(Double.parseDouble(newValue)))
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setHeader(String.format("Експерт %d", index + 1));

            evaluationColumns.add(column);
        }


        HeaderRow headerRow = riskEventsGrid.prependHeaderRow();
        headerRow.join(evaluationColumns.toArray(Column[]::new)).setText("Оцінки експертів");

        Column<?>[] weightedEvaluationColumns = IntStream.range(0, EVALUATIONS_COUNT)
                .mapToObj(i -> new NumberRenderer<RiskEvent>(riskEvent -> riskEvent.getProbabilityEvaluations().get(i).getWeightedValue(), "%(.2f", Locale.US))
                .map(riskEventsGrid::addColumn)
                .toArray(Column[]::new);

        IntStream.range(0, EVALUATIONS_COUNT)
                .forEach(i -> weightedEvaluationColumns[i].setTextAlign(ColumnTextAlign.CENTER).setHeader(String.format("Експерт %d", i + 1)));

        headerRow.join(weightedEvaluationColumns).setText("Оцінки експертів з урахуванням вагомості");

        riskEventsGrid.addColumn(new NumberRenderer<>(RiskEvent::getWeightedRiskProbability, "%(.2f", Locale.US))
                .setComparator(Comparator.comparingDouble(RiskEvent::getWeightedRiskProbability))
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(10)
                .setHeader("Зважена ймовірність");

        riskEventsGrid.addColumn(riskEvent -> ProbabilityBracket.getBracket(riskEvent.getWeightedRiskProbability()))
                .setComparator(Comparator.comparingDouble(RiskEvent::getWeightedRiskProbability))
                .setClassNameGenerator(riskEvent -> ProbabilityBracket.getBracket(riskEvent.getWeightedRiskProbability()).name().toLowerCase())
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(10)
                .setHeader("Ймовірність виникнення");

        riskEventsGrid.addItemPropertyChangedListener(event -> {
            System.out.println(riskEvents);
            System.out.println(event);
        });

        add(new H1("Етап 2.1. Визначення ймовірності настання ризикових подій"));
        add(new H2("Класифікація настання ризикових подій"));
        add(riskEventsGrid);
        add(new H2("Коефіцієнти вагомості експертів для ймовірності настання ризикових подій"));
        add(evaluationsGrid);
    }
}
