package org.monjasa.application.views.pages.risk;

import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.monjasa.application.model.risk.RiskEvent;
import org.monjasa.application.model.risk.RiskType;
import org.monjasa.application.service.RiskEventService;
import org.monjasa.application.views.MainView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.text.NumberFormat;
import java.util.Locale;

@Route(value = "risk-events", layout = MainView.class)
@PageTitle("Потенційні ризикові події")
public class RiskEventsView extends VerticalLayout {

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired private RiskEventService riskEventService;

    private final GridPro<RiskEvent> riskEventsGrid;
    private final Grid<RiskType> riskTypesGrid;

    @PostConstruct
    public void initializeDataProvider() {

        riskEventsGrid.setDataProvider(riskEventService.getDataProviderFromCallbacks());

        NumberFormat percentInstance = NumberFormat.getPercentInstance(Locale.ENGLISH);
        percentInstance.setMaximumFractionDigits(2);

        Column<RiskType> riskTypeColumn = riskTypesGrid.addColumn(RiskType::toString)
                .setHeader("Множина ризикових подій");
        Column<RiskType> countColumn = riskTypesGrid.addColumn(new NumberRenderer<>(riskType -> riskEventService.findAssessedByRiskType(riskType).size(), "%d"))
                .setHeader("Кількість наявних подій");
        Column<RiskType> percentColumn = riskTypesGrid.addColumn(new NumberRenderer<>(riskType -> (double) riskEventService.findAssessedByRiskType(riskType).size() / riskEventService.countAll(), percentInstance))
                .setHeader("Ймовірність появи ризику");

        FooterRow footerRow = riskTypesGrid.prependFooterRow();
        footerRow.getCell(riskTypeColumn).setText("Загалом");

        riskTypesGrid.getDataProvider().addDataProviderListener(event -> {
            long riskEventsCount = riskEventService.countAll();
            long assessedRiskEventsCount = riskEventService.countByPredicate(RiskEvent::isAssessed);

            footerRow.getCell(countColumn).setText(String.valueOf(assessedRiskEventsCount));
            footerRow.getCell(percentColumn).setText(String.format(Locale.US, "%.2f%%", 100f * assessedRiskEventsCount / riskEventsCount));
        });

        riskTypesGrid.getDataProvider().refreshAll();
    }


    public RiskEventsView() {

        setId("risk-events-view");

        riskEventsGrid = new GridPro<>();
        riskEventsGrid.addThemeName("row-stripes");
        riskEventsGrid.addThemeName("wrap-cell-content");
        riskEventsGrid.getStyle().set("margin-bottom", "2em");

        riskEventsGrid.addEditColumn(RiskEvent::getRiskType)
                .select(RiskEvent::setRiskType, RiskType.class)
                .setSortProperty("riskType")
                .setHeader("Тип ризиків");
        riskEventsGrid.addEditColumn(RiskEvent::getName)
                .text(RiskEvent::setName)
                .setSortProperty("name")
                .setFlexGrow(10)
                .setHeader("Назва події");
        riskEventsGrid.addEditColumn(RiskEvent::isAssessed, new TextRenderer<>(item -> item.isAssessed() ? "Так" : "Ні"))
                .checkbox(RiskEvent::setAssessed)
                .setSortProperty("assessed")
                .setComparator((firstRiskEvent, secondRiskEvent) -> Boolean.compare(firstRiskEvent.isAssessed(), secondRiskEvent.isAssessed()))
                .setHeader("Наявність події");

        riskTypesGrid = new Grid<>();
        riskTypesGrid.addThemeName("row-stripes");
        riskTypesGrid.addThemeName("wrap-cell-content");
        riskTypesGrid.setHeightByRows(true);

        riskTypesGrid.setItems(RiskType.values());

        riskEventsGrid.addItemPropertyChangedListener(event -> {
            riskEventService.save(event.getItem());
            riskTypesGrid.getDataProvider().refreshAll();
        });

        add(new H1("Етап 1.2. Ідентифікація потенційних ризикових подій"));
        add(new H2("Множини настання потенційних ризикових подій"));
        add(riskTypesGrid);
        add(new H2("Модель ідентифікації потенційних ризикових подій"));
        add(riskEventsGrid);
    }
}
