package org.monjasa.application.views.pages;

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

        ObjectMapper objectMapper = new ObjectMapper();
        List<RiskEvent> riskEvents = new ArrayList<>(List.of(
                new RiskEvent(RiskType.TECHNICAL, "Ризикова подія 01", true, new BigDecimal(0), new ArrayList<>(), null),
                new RiskEvent(RiskType.TECHNICAL, "Ризикова подія 02", false, new BigDecimal(0), new ArrayList<>(), null),
                new RiskEvent(RiskType.BUDGET, "Ризикова подія 03", true, new BigDecimal(0), new ArrayList<>(), null),
                new RiskEvent(RiskType.SCHEDULE, "Ризикова подія 04", true, new BigDecimal(0), new ArrayList<>(), null),
                new RiskEvent(RiskType.OPERATIONAL, "Ризикова подія 05", false, new BigDecimal(0), new ArrayList<>(), null),
                new RiskEvent(RiskType.OPERATIONAL, "Ризикова подія 06", true, new BigDecimal(0), new ArrayList<>(), null),
                new RiskEvent(RiskType.OPERATIONAL, "Ризикова подія 07", true, new BigDecimal(0), new ArrayList<>(), null)
        ));

//        try {
//            riskEvents.addAll(objectMapper.readValue(
//                    RiskSourcesView.class.getClassLoader().getResourceAsStream("data/risk-events.json"),
//                    objectMapper.getTypeFactory().constructCollectionType(List.class, RiskEvent.class)
//            ));
//        } catch (IOException exception) {
//            exception.printStackTrace();
//        }

        GridPro<RiskEvent> riskSourcesGrid = new GridPro<>();
        riskSourcesGrid.setDataProvider(new ListDataProvider<>(riskEvents));

        riskSourcesGrid.addColumn(RiskEvent::getRiskType, "riskType").setHeader("Тип ризиків");
        riskSourcesGrid.addColumn(RiskEvent::getName, "name").setHeader("Назва події");
        riskSourcesGrid.addEditColumn(RiskEvent::isAssessed, new TextRenderer<>(item -> item.isAssessed() ? "Так" : "Ні"))
                .checkbox(RiskEvent::setAssessed)
                .setComparator((o1, o2) -> Boolean.compare(o1.isAssessed(), o2.isAssessed()))
                .setHeader("Наявність події");

        Grid<RiskEventsSetData> riskSourceSetsGrid = new Grid<>();
        riskSourceSetsGrid.setItems(riskSourceSets);
        riskSourceSetsGrid.setHeightByRows(true);

        long riskSourcesCount = riskEvents.stream().filter(RiskEvent::isAssessed).count();

        NumberFormat percentInstance = NumberFormat.getPercentInstance(Locale.ENGLISH);
        percentInstance.setMaximumFractionDigits(2);

        riskSourceSetsGrid.addColumn(RiskEventsSetData::getRiskType).setHeader("Тип ризиків");
        riskSourceSetsGrid.addColumn(RiskEventsSetData::getAbsoluteValue)
                .setHeader("Кількість подій")
                .setFooter(String.valueOf(riskSourcesCount));
        riskSourceSetsGrid.addColumn(new NumberRenderer<>(RiskEventsSetData::getRelativeValue, percentInstance))
                .setHeader("Ймовірність появи")
                .setFooter(String.format(Locale.US, "%.2f%%", 100f * riskSourcesCount / 18));

        add(new H1("Ідентифікація потенційних ризикових подій"));
        add(riskSourcesGrid);
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
