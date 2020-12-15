package org.monjasa.application.views.pages;

import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.monjasa.application.model.RiskEvent;
import org.monjasa.application.model.RiskType;
import org.monjasa.application.model.bracket.ProbabilityBracket;
import org.monjasa.application.service.RiskEventService;
import org.monjasa.application.views.MainView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Route(value = "risk-event-probabilities", layout = MainView.class)
@PageTitle("Ймовірності настання ризикових подій")
public class RiskEventProbabilitiesView extends VerticalLayout {

    public static final int EVALUATIONS_COUNT = 5;

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired private RiskEventService riskEventService;

    private final GridPro<RiskEvent> riskEventsGrid;

    @PostConstruct
    public void initializeDataProvider() {
        riskEventsGrid.setDataProvider(riskEventService.getDataProviderFromCallbacks());
    }

    public RiskEventProbabilitiesView() {

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
                    .setAutoWidth(true)
                    .setHeader(String.format("Е-%d", index + 1));

            evaluationColumns.add(column);
        }

        for (int i = 0; i < EVALUATIONS_COUNT; i++) {
            final int index = i;
            Column<RiskEvent> column = riskEventsGrid.addColumn(new NumberRenderer<>(riskEvent -> riskEvent.getProbabilityEvaluations().get(index).getWeightedValue(), "%(.2f", Locale.US))
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setAutoWidth(true)
                    .setHeader(String.format("Е-%d", index + 1));

            weightedEvaluationColumns.add(column);
        }

        HeaderRow headerRow = riskEventsGrid.prependHeaderRow();
        headerRow.join(evaluationColumns.toArray(Column[]::new)).setText("Оцінки експертів");
        headerRow.join(weightedEvaluationColumns.toArray(Column[]::new)).setText("Оцінки експертів з урахуванням їх вагомості");

        riskEventsGrid.addColumn(new NumberRenderer<>(RiskEvent::getWeightedRiskProbability, "%(.2f", Locale.US))
                .setTextAlign(ColumnTextAlign.CENTER)
                .setHeader("Зважена ймовірність");

        riskEventsGrid.addColumn(riskEvent -> ProbabilityBracket.getBracket(riskEvent.getWeightedRiskProbability()))
                .setClassNameGenerator(riskEvent -> ProbabilityBracket.getBracket(riskEvent.getWeightedRiskProbability()).name().toLowerCase())
                .setTextAlign(ColumnTextAlign.CENTER)
                .setHeader("Ймовірність виникнення");

        riskEventsGrid.addItemPropertyChangedListener(event -> riskEventService.save(event.getItem()));

        add(new H1("Етап 2.1. Визначення ймовірності настання ризикових подій"));
        add(riskEventsGrid);
    }
}
