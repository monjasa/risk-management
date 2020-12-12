package org.monjasa.application.views.pages;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.monjasa.application.model.RiskSource;
import org.monjasa.application.views.MainView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Route(value = "risk-sources", layout = MainView.class)
@PageTitle("Джерела появи ризиків")
@RouteAlias(value = "", layout = MainView.class)
public class RiskSourcesView extends VerticalLayout {

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
        riskSourcesGrid.addThemeName("row-stripes");
        riskSourcesGrid.addThemeName("wrap-cell-content");

        riskSourcesGrid.setDataProvider(new ListDataProvider<>(riskEvents));

        riskSourcesGrid.addColumn(RiskSource::getRiskType, "riskType").setHeader("Тип ризиків");
        riskSourcesGrid.addColumn(RiskSource::getName, "name")
                .setFlexGrow(10)
                .setHeader("Назва джерела");
        riskSourcesGrid.addEditColumn(RiskSource::isAssessed, new TextRenderer<>(item -> item.isAssessed() ? "Так" : "Ні"))
                .checkbox(RiskSource::setAssessed)
                .setComparator((firstRiskSource, secondRiskSource) -> Boolean.compare(firstRiskSource.isAssessed(), secondRiskSource.isAssessed()))
                .setHeader("Наявність джерела");

//        Grid<RiskSourcesSetData> riskSourceSetsGrid = new Grid<>();
//        riskSourceSetsGrid.addThemeName("row-stripes");
//        riskSourceSetsGrid.addThemeName("wrap-cell-content");
//        riskSourceSetsGrid.setHeightByRows(true);
//
//        riskSourceSetsGrid.setItems(RISK_SOURCES_SET_DATA);
//
//        long riskSourcesCount = riskEvents.stream().filter(RiskSource::isAssessed).count();
//
//        NumberFormat percentInstance = NumberFormat.getPercentInstance(Locale.ENGLISH);
//        percentInstance.setMaximumFractionDigits(2);
//
//        riskSourceSetsGrid.addColumn(RiskSourcesSetData::getRiskType).setHeader("Множина джерел появи ризиків");
//        riskSourceSetsGrid.addColumn(RiskSourcesSetData::getAbsoluteValue)
//                .setHeader("Кількість джерел")
//                .setFooter(String.valueOf(riskSourcesCount));
//        riskSourceSetsGrid.addColumn(new NumberRenderer<>(RiskSourcesSetData::getRelativeValue, percentInstance))
//                .setHeader("Ймовірність появи")
//                .setFooter(String.format(Locale.US, "%.2f%%", 100f * riskSourcesCount / 18));

        add(new H1("Етап 1.1. Визначення можливих джерел появи ризиків"));
        add(new H2("Модель можливих джерел появи ризиків розроблення ПЗ"));
        add(riskSourcesGrid);
        add(new H2("Множини джерел появи ризиків"));
//        add(riskSourceSetsGrid);
    }
}
