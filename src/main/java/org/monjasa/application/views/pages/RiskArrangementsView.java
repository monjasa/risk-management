package org.monjasa.application.views.pages;

import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.monjasa.application.model.Arrangement;
import org.monjasa.application.model.RiskEvent;
import org.monjasa.application.model.RiskType;
import org.monjasa.application.service.ArrangementService;
import org.monjasa.application.service.RiskEventService;
import org.monjasa.application.views.MainView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;

@Route(value = "risk-arrangements", layout = MainView.class)
@PageTitle("Заходи із зменшення або усунення ризику")
public class RiskArrangementsView extends VerticalLayout {

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    private RiskEventService riskEventService;

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    private ArrangementService arrangementService;

    private final GridPro<RiskEvent> riskEventsGrid;

    @PostConstruct
    public void initializeDataProvider() {

        riskEventsGrid.setDataProvider(riskEventService.getDataProviderFromCallbacks());

        riskEventsGrid.addEditColumn(RiskEvent::getArrangement, new ComponentRenderer<>(this::getArrangementLabel))
                .select((item, newValue) -> item.setArrangement(arrangementService.findByName(newValue)), arrangementService.findAll().stream().map(Arrangement::getName).collect(Collectors.toList()))
                .setSortProperty("arrangement")
                .setFlexGrow(10)
                .setHeader("Заходи зі змешення ризику");
    }

    private Label getArrangementLabel(RiskEvent riskEvent) {

        if (riskEvent.getArrangement() == null) {
            Label label = new Label("Призначити захід зі зменшення або усунення ризику...");
            label.getStyle().set("color", "gray");
            return label;
        }

        return new Label(riskEvent.getArrangement().getName());
    }

    public RiskArrangementsView() {

        setId("risk-arrangements-view");

        riskEventsGrid = new GridPro<>();
        riskEventsGrid.setHeightByRows(true);
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

        riskEventsGrid.addItemPropertyChangedListener(event -> {
            riskEventService.save(event.getItem());
        });

        add(new H1("Етап 3. Планування ризиків розроблення ПЗ"));
        add(riskEventsGrid);
    }
}
