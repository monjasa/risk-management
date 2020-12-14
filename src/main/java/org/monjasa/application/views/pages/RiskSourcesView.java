package org.monjasa.application.views.pages;

import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.monjasa.application.model.RiskEvent;
import org.monjasa.application.model.RiskSource;
import org.monjasa.application.model.RiskType;
import org.monjasa.application.service.RiskSourceService;
import org.monjasa.application.views.MainView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import javax.annotation.PostConstruct;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.vaadin.flow.data.provider.SortDirection.ASCENDING;

@Route(value = "risk-sources", layout = MainView.class)
@PageTitle("Джерела появи ризиків")
@RouteAlias(value = "", layout = MainView.class)
public class RiskSourcesView extends VerticalLayout {


    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    private RiskSourceService riskSourceService;

    private final GridPro<RiskSource> riskSourcesGrid;
    private final Grid<RiskType> riskTypesGrid;

    @PostConstruct
    public void initializeDataProvider() {

        riskSourcesGrid.setDataProvider(DataProvider.fromCallbacks(
                query -> {
                    Optional<Sort> sort = query.getSortOrders().stream()
                            .map(queryOrder -> Sort.by(queryOrder.getDirection() == ASCENDING ? Direction.ASC : Direction.DESC, queryOrder.getSorted()))
                            .findFirst();

                    int offset = query.getOffset();
                    int limit = query.getLimit();

                    List<RiskSource> riskSources = sort.isPresent() ? riskSourceService.findAll(sort.get()) : riskSourceService.findAll();
                    return riskSources.stream();
                }, query -> Math.toIntExact(riskSourceService.countAll()))
        );

        long riskSourcesCount = riskSourceService.countAll();

        NumberFormat percentInstance = NumberFormat.getPercentInstance(Locale.ENGLISH);
        percentInstance.setMaximumFractionDigits(2);

        Column<RiskType> riskTypeColumn = riskTypesGrid.addColumn(RiskType::toString)
                .setHeader("Множина джерел");
        Column<RiskType> countColumn = riskTypesGrid.addColumn(new NumberRenderer<>(riskType -> riskSourceService.findAssessedByRiskType(riskType).size(), "%d"))
                .setHeader("Кількість наявних джерел");
        Column<RiskType> percentColumn = riskTypesGrid.addColumn(new NumberRenderer<>(riskType -> (double) riskSourceService.findAssessedByRiskType(riskType).size() / riskSourcesCount, percentInstance))
                .setHeader("Ймовірність появи джерела");

        FooterRow footerRow = riskTypesGrid.prependFooterRow();
        footerRow.getCell(riskTypeColumn).setText("Загалом");

        riskTypesGrid.getDataProvider().addDataProviderListener(event -> {
            long riskEventsCount = riskSourceService.countAll();
            long assessedRiskEventsCount = riskSourceService.countByPredicate(RiskSource::isAssessed);

            footerRow.getCell(countColumn).setText(String.valueOf(assessedRiskEventsCount));
            footerRow.getCell(percentColumn).setText(String.format(Locale.US, "%.2f%%", 100f * assessedRiskEventsCount / riskEventsCount));
        });

        riskTypesGrid.getDataProvider().refreshAll();
    }


    public RiskSourcesView() {

        setId("risk-sources-view");

        riskSourcesGrid = new GridPro<>();
        riskSourcesGrid.addThemeName("row-stripes");
        riskSourcesGrid.addThemeName("wrap-cell-content");
        riskSourcesGrid.getStyle().set("margin-bottom", "2em");

        riskSourcesGrid.addEditColumn(RiskSource::getRiskType)
                .select(RiskSource::setRiskType, RiskType.class)
                .setSortProperty("riskType")
                .setHeader("Тип ризиків");
        riskSourcesGrid.addEditColumn(RiskSource::getName)
                .text(RiskSource::setName)
                .setSortProperty("name")
                .setFlexGrow(10)
                .setHeader("Назва джерела");
        riskSourcesGrid.addEditColumn(RiskSource::isAssessed, new TextRenderer<>(item -> item.isAssessed() ? "Так" : "Ні"))
                .checkbox(RiskSource::setAssessed)
                .setSortProperty("assessed")
                .setHeader("Наявність джерела");

        riskTypesGrid = new Grid<>();
        riskTypesGrid.addThemeName("row-stripes");
        riskTypesGrid.addThemeName("wrap-cell-content");
        riskTypesGrid.setHeightByRows(true);

        riskTypesGrid.setItems(RiskType.values());

        riskSourcesGrid.addItemPropertyChangedListener(event -> {
            riskSourceService.save(event.getItem());
            riskTypesGrid.getDataProvider().refreshAll();
        });

        add(new H1("Етап 1.1. Визначення можливих джерел появи ризиків"));
        add(new H2("Множини джерел появи ризиків"));
        add(riskTypesGrid);
        add(new H2("Модель можливих джерел появи ризиків розроблення ПЗ"));
        add(riskSourcesGrid);
    }
}
