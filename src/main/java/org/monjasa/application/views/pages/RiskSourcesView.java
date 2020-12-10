package org.monjasa.application.views.pages;

import org.monjasa.application.model.RiskSource;
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
import com.vaadin.flow.router.RouteAlias;
import lombok.Data;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;

@Route(value = "risk-sources", layout = MainView.class)
@PageTitle("Джерела появи ризиків")
@RouteAlias(value = "", layout = MainView.class)
public class RiskSourcesView extends VerticalLayout {

    public static final List<RiskSourcesSetData> RISK_SOURCES_SET_DATA = new ArrayList<>(List.of(
            new RiskSourcesSetData(RiskType.TECHNICAL, 6),
            new RiskSourcesSetData(RiskType.BUDGET, 2),
            new RiskSourcesSetData(RiskType.SCHEDULE, 2),
            new RiskSourcesSetData(RiskType.OPERATIONAL, 3)
    ));

    public RiskSourcesView() {

        setId("risk-sources-view");

        ObjectMapper objectMapper = new ObjectMapper();
        List<RiskSource> riskEvents = new ArrayList<>();

        try {
            riskEvents.addAll(objectMapper.readValue(
                    RiskSourcesView.class.getClassLoader().getResourceAsStream("data/risk-sources.json"),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, RiskSource.class)
            ));
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        GridPro<RiskSource> riskSourcesGrid = new GridPro<>();
        riskSourcesGrid.setDataProvider(new ListDataProvider<>(riskEvents));

        riskSourcesGrid.addColumn(RiskSource::getRiskType, "riskType").setHeader("Тип ризиків");
        riskSourcesGrid.addColumn(RiskSource::getName, "name").setHeader("Назва джерела");
        riskSourcesGrid.addEditColumn(RiskSource::isAssessed, new TextRenderer<>(item -> item.isAssessed() ? "Так" : "Ні"))
                .checkbox(RiskSource::setAssessed)
                .setComparator((o1, o2) -> Boolean.compare(o1.isAssessed(), o2.isAssessed()))
                .setHeader("Наявність джерела");

        Grid<RiskSourcesSetData> riskSourceSetsGrid = new Grid<>();
        riskSourceSetsGrid.setItems(RISK_SOURCES_SET_DATA);
        riskSourceSetsGrid.setHeightByRows(true);

        long riskSourcesCount = riskEvents.stream().filter(RiskSource::isAssessed).count();

        NumberFormat percentInstance = NumberFormat.getPercentInstance(Locale.ENGLISH);
        percentInstance.setMaximumFractionDigits(2);

        riskSourceSetsGrid.addColumn(RiskSourcesSetData::getRiskType).setHeader("Тип ризиків");
        riskSourceSetsGrid.addColumn(RiskSourcesSetData::getAbsoluteValue)
                .setHeader("Кількість джерел")
                .setFooter(String.valueOf(riskSourcesCount));
        riskSourceSetsGrid.addColumn(new NumberRenderer<>(RiskSourcesSetData::getRelativeValue, percentInstance))
                .setHeader("Ймовірність появи")
                .setFooter(String.format(Locale.US, "%.2f%%", 100f * riskSourcesCount / 18));

        add(new H1("Ідентифікація можливих джерел появи ризиків"));
        add(riskSourcesGrid);
        add(riskSourceSetsGrid);
    }

    @Data
    private static class RiskSourcesSetData {
        private RiskType riskType;
        private int absoluteValue;
        private double relativeValue;

        public RiskSourcesSetData(RiskType riskType, int absoluteValue) {
            this.riskType = riskType;
            this.absoluteValue = absoluteValue;
            this.relativeValue = absoluteValue / 18f;
        }
    }
}
