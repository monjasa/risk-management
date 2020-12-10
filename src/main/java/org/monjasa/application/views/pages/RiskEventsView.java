package org.monjasa.application.views.pages;

import com.vaadin.flow.component.html.H2;
import org.monjasa.application.model.RiskEvent;
import org.monjasa.application.model.RiskType;
import org.monjasa.application.views.MainView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.Data;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Route(value = "risk-events", layout = MainView.class)
@PageTitle("Потенційні ризикові події")
public class RiskEventsView extends VerticalLayout {

    public static final List<RiskEventsSetData> riskSourceSets = new ArrayList<>(List.of(
            new RiskEventsSetData(RiskType.TECHNICAL, 9),
            new RiskEventsSetData(RiskType.BUDGET, 7),
            new RiskEventsSetData(RiskType.SCHEDULE, 6),
            new RiskEventsSetData(RiskType.OPERATIONAL, 12)
    ));

    public RiskEventsView() {

        setId("risk-events-view");

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

        GridPro<RiskEvent> riskEventsGrid = new GridPro<>();
        riskEventsGrid.addThemeName("row-stripes");
        riskEventsGrid.addThemeName("wrap-cell-content");

        riskEventsGrid.setDataProvider(new ListDataProvider<>(riskEvents));

        riskEventsGrid.addColumn(RiskEvent::getRiskType, "riskType").setHeader("Тип ризиків");
        riskEventsGrid.addColumn(RiskEvent::getName, "name")
                .setFlexGrow(10)
                .setHeader("Назва події");
        riskEventsGrid.addEditColumn(RiskEvent::isAssessed, new TextRenderer<>(item -> item.isAssessed() ? "Так" : "Ні"))
                .checkbox(RiskEvent::setAssessed)
                .setComparator((o1, o2) -> Boolean.compare(o1.isAssessed(), o2.isAssessed()))
                .setHeader("Наявність події");

        Grid<RiskEventsSetData> riskSourceSetsGrid = new Grid<>();

        riskSourceSetsGrid.setItems(riskSourceSets);
        riskSourceSetsGrid.addThemeName("row-stripes");
        riskSourceSetsGrid.addThemeName("wrap-cell-content");
        riskSourceSetsGrid.setHeightByRows(true);

        long riskSourcesCount = riskEvents.stream().filter(RiskEvent::isAssessed).count();

        NumberFormat percentInstance = NumberFormat.getPercentInstance(Locale.ENGLISH);
        percentInstance.setMaximumFractionDigits(2);

        riskSourceSetsGrid.addColumn(RiskEventsSetData::getRiskType).setHeader("Множина ризикових подій");
        riskSourceSetsGrid.addColumn(RiskEventsSetData::getAbsoluteValue)
                .setHeader("Кількість подій")
                .setFooter(String.valueOf(riskSourcesCount));
        riskSourceSetsGrid.addColumn(new NumberRenderer<>(RiskEventsSetData::getRelativeValue, percentInstance))
                .setHeader("Ймовірність появи")
                .setFooter(String.format(Locale.US, "%.2f%%", 100f * riskSourcesCount / 18));

        add(new H1("Етап 1.2. Ідентифікація потенційних ризикових подій"));
        add(new H2("Модель ідентифікації потенційних ризикових подій"));
        add(riskEventsGrid);
        add(new H2("Множини настання потенційних ризикових подій"));
        add(riskSourceSetsGrid);
    }

    @Data
    private static class RiskEventsSetData {
        private RiskType riskType;
        private int absoluteValue;
        private double relativeValue;

        public RiskEventsSetData(RiskType riskType, int absoluteValue) {
            this.riskType = riskType;
            this.absoluteValue = absoluteValue;
            this.relativeValue = absoluteValue / 41f;
        }
    }
}
