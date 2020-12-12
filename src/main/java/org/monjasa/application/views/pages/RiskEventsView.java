package org.monjasa.application.views.pages;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.monjasa.application.model.RiskEvent;
import org.monjasa.application.model.RiskType;
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

        riskEventsGrid.setDataProvider(DataProvider.fromCallbacks(
                query -> {
                    int offset = query.getOffset();
                    int limit = query.getLimit();
                    return riskEventService.findAll().stream();
                }, query -> Math.toIntExact(riskEventService.countAll()))
        );

        long riskEventsCount = riskEventService.countAll();
        long assessedRiskEventsCount = riskEventService.countByPredicate(RiskEvent::isAssessed);

        NumberFormat percentInstance = NumberFormat.getPercentInstance(Locale.ENGLISH);
        percentInstance.setMaximumFractionDigits(2);

        riskTypesGrid.addColumn(RiskType::toString).setHeader("Множина ризикових подій");
        riskTypesGrid.addColumn(new NumberRenderer<>(riskType -> riskEventService.findAssessedByRiskType(riskType).size(), "%d"))
                .setHeader("Кількість наявних подій");
        riskTypesGrid.addColumn(new NumberRenderer<>(riskType -> (double) riskEventService.findAssessedByRiskType(riskType).size() / riskEventsCount, percentInstance))
                .setHeader("Ймовірність появи ризику");
    }


    public RiskEventsView() {

        setId("risk-events-view");

        riskEventsGrid = new GridPro<>();
        riskEventsGrid.addThemeName("row-stripes");
        riskEventsGrid.addThemeName("wrap-cell-content");

        riskEventsGrid.addColumn(RiskEvent::getRiskType, "riskType").setHeader("Тип ризиків");
        riskEventsGrid.addColumn(RiskEvent::getName, "name")
                .setFlexGrow(10)
                .setHeader("Назва події");
        riskEventsGrid.addEditColumn(RiskEvent::isAssessed, new TextRenderer<>(item -> item.isAssessed() ? "Так" : "Ні"))
                .checkbox(RiskEvent::setAssessed)
                .setComparator((firstRiskEvent, secondRiskEvent) -> Boolean.compare(firstRiskEvent.isAssessed(), secondRiskEvent.isAssessed()))
                .setHeader("Наявність події");

        riskTypesGrid = new Grid<>();

        riskTypesGrid.setItems(RiskType.values());
        riskTypesGrid.addThemeName("row-stripes");
        riskTypesGrid.addThemeName("wrap-cell-content");
        riskTypesGrid.setHeightByRows(true);

        riskEventsGrid.addItemPropertyChangedListener(event -> {
            riskEventService.save(event.getItem());
            riskTypesGrid.getDataProvider().refreshAll();
        });

        add(new H1("Етап 1.2. Ідентифікація потенційних ризикових подій"));
        add(new H2("Модель ідентифікації потенційних ризикових подій"));
        add(riskEventsGrid);
        add(new H2("Множини настання потенційних ризикових подій"));
        add(riskTypesGrid);
    }
}
