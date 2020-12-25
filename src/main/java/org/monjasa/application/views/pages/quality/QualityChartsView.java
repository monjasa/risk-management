package org.monjasa.application.views.pages.quality;

import antlr.collections.impl.IntRange;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.monjasa.application.model.Evaluation;
import org.monjasa.application.model.quality.AttributeType;
import org.monjasa.application.model.quality.Expert;
import org.monjasa.application.model.quality.ExpertType;
import org.monjasa.application.model.risk.RiskEvent;
import org.monjasa.application.service.ExpertService;
import org.monjasa.application.views.MainView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Route(value = "quality-charts", layout = MainView.class)
@PageTitle("Комплексні показники якості")
public class QualityChartsView extends VerticalLayout {

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    private ExpertService expertService;

    private final Map<ExpertType, GridPro<AttributeType>> attributeTypesGrids;
    private final Map<ExpertType, Chart> charts;

    @PostConstruct
    public void initializeDataProvider() {

        int totalWeight = expertService.findAll().stream()
                .mapToInt(Expert::getWeight)
                .sum();

        Map<AttributeType, Evaluation> totalAttributeEvaluations = new HashMap<>();

        for (AttributeType attributeType : AttributeType.values()) {

            double averageWeight = expertService.findAll().stream()
                    .map(Expert::getAttributeEvaluations)
                    .map(attributeTypeEvaluationMap -> attributeTypeEvaluationMap.get(attributeType))
                    .mapToDouble(Evaluation::getWeight)
                    .average()
                    .orElse(1.0);

            double sum = expertService.findAll().stream()
                    .mapToDouble(expert -> expert.normalizeEvaluationForAttribute(attributeType))
                    .sum();

            Evaluation evaluation = new Evaluation(1000L + attributeType.ordinal(), sum, averageWeight);

            totalAttributeEvaluations.put(attributeType, evaluation);
        }

        Expert totalExpert = Expert.builder()
                .expertType(ExpertType.TOTAL)
                .weight(totalWeight)
                .attributeEvaluations(totalAttributeEvaluations)
                .build();


        for (ExpertType expertType : ExpertType.values()) {

            GridPro<AttributeType> attributeTypesGrid = attributeTypesGrids.get(expertType);
            Chart chart = charts.get(expertType);

            Expert expert = expertType == ExpertType.TOTAL
                    ? totalExpert
                    : expertService.findByExpertType(expertType);
            Map<AttributeType, Evaluation> attributeEvaluations = expert.getAttributeEvaluations();

            Column<AttributeType> attributeTypeColumn = attributeTypesGrid.addColumn(AttributeType::toString)
                    .setSortProperty("name")
                    .setHeader("Критерій оцінювання");
            Column<AttributeType> evaluationValueColumn = attributeTypesGrid.addEditColumn(
                    attributeType -> attributeEvaluations.get(attributeType).getValue(),
                    new NumberRenderer<>(attributeType -> attributeEvaluations.get(attributeType).getValue(), "%(.2f", Locale.US)
            )
                    .text((item, newValue) -> attributeEvaluations.get(item).setValue(Double.parseDouble(newValue)))
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setAutoWidth(true)
                    .setHeader("Оцінка");
            attributeTypesGrid.addEditColumn(attributeType -> attributeEvaluations.get(attributeType).getWeight())
                    .text((item, newValue) -> attributeEvaluations.get(item).setWeight(Integer.parseInt(newValue)))
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setAutoWidth(true)
                    .setHeader("Вагомість");

            attributeTypesGrid.addColumn(new NumberRenderer<>(expert::getAngleForAttributeSector, "%.2f°", Locale.US))
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setAutoWidth(true)
                    .setHeader("Частка сектора");
            attributeTypesGrid.addColumn(new NumberRenderer<>(expert::getAngleForAttributeSectorStart, "%.2f°", Locale.US))
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setAutoWidth(true)
                    .setHeader("Початок сектора");
            Column<AttributeType> vectorAngleColumn = attributeTypesGrid.addColumn(new NumberRenderer<>(expert::getAngleForAttributeVector, "%.2f°", Locale.US))
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setAutoWidth(true)
                    .setHeader("Кут вектора");
            Column<AttributeType> areaColumn = attributeTypesGrid.addColumn(new NumberRenderer<>(expert::getAreaForAttribute, "%.4f", Locale.US))
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setAutoWidth(true)
                    .setHeader("Площа");

            FooterRow footerRow = attributeTypesGrid.prependFooterRow();
            footerRow.getCell(attributeTypeColumn).setText("Усереднена оцінка");
            footerRow.getCell(vectorAngleColumn).setText("Комплексний показник");

            DataSeries evaluationsDataSeries = new DataSeries(expert.processEvaluationDataItems());
            evaluationsDataSeries.setName("Комплексний показник якості ПЗ");
            chart.getConfiguration().setSeries(evaluationsDataSeries);

            attributeTypesGrid.addItemPropertyChangedListener(event -> {
                expertService.save(expert);
                attributeTypesGrid.getDataProvider().refreshAll();

                evaluationsDataSeries.clear();
                evaluationsDataSeries.updateSeries();
                expert.processEvaluationDataItems().forEach(evaluationsDataSeries::add);
            });

            attributeTypesGrid.getDataProvider().addDataProviderListener(event -> {
                double averageValue = expert.getAttributeEvaluations().values().stream()
                        .mapToDouble(Evaluation::getValue)
                        .average()
                        .orElse(1.0);

                double area = Arrays.stream(AttributeType.values())
                        .mapToDouble(expert::getAreaForAttribute)
                        .sum();

                footerRow.getCell(evaluationValueColumn).setText(String.format(Locale.US, "%.2f", averageValue));
                footerRow.getCell(areaColumn).setText(String.format(Locale.US, "%.2f%%", 100f * area / Math.PI));
            });

            attributeTypesGrid.getDataProvider().refreshAll();
        }
    }

    public QualityChartsView() {

        setId("quality-charts-view");

        attributeTypesGrids = new HashMap<>(ExpertType.values().length);
        charts = new HashMap<>(ExpertType.values().length);

        Map<ExpertType, HorizontalLayout> horizontalLayouts = new HashMap<>();

        for (ExpertType expertType : ExpertType.values()) {

            GridPro<AttributeType> attributeTypesGrid = new GridPro<>();

            attributeTypesGrid.setHeightByRows(true);
            attributeTypesGrid.setWidth("1100px");
            attributeTypesGrid.addThemeName("row-stripes");
            attributeTypesGrid.addThemeName("wrap-cell-content");

            attributeTypesGrid.setDataProvider(new ListDataProvider<>(List.of(AttributeType.values())));

            Chart chart = new Chart(ChartType.AREA);
            chart.setWidth("500px");
            Configuration configuration = chart.getConfiguration();

            configuration.setTitle("Полярна діаграма для експерту галузі");
            configuration.setTitle(String.format("Полярна діаграма (%s)", expertType.toString().toLowerCase(Locale.ROOT)));
            configuration.getChart().setPolar(true);

            Labels xLabels = new Labels();
            xLabels.setFormatter("function() { return this.value + '°'; }");

            XAxis xAxis = new XAxis();
            xAxis.setReversed(true);
            xAxis.setTickInterval(45);
            xAxis.setMin(0);
            xAxis.setMax(360);
            xAxis.setLabels(xLabels);

            Labels yLabels = new Labels();
            yLabels.setFormatter("function() { if (this.value == 0) return ''; else return (Math.round(this.value * 100) / 100).toFixed(2); }");

            YAxis yAxis = new YAxis();
            yAxis.setTickInterval(0.25);
            yAxis.setMin(0);
            yAxis.setMax(1);
            yAxis.setLabels(yLabels);

            PlotOptionsArea plotOptionsArea = new PlotOptionsArea();
            configuration.setPlotOptions(plotOptionsArea);

            configuration.addxAxis(xAxis);
            configuration.addyAxis(yAxis);

            attributeTypesGrids.put(expertType, attributeTypesGrid);
            charts.put(expertType, chart);

            HorizontalLayout horizontalLayout = new HorizontalLayout(attributeTypesGrid, chart);
            horizontalLayout.expand(attributeTypesGrid);

            horizontalLayouts.put(expertType, horizontalLayout);
        }

        add(new H1("Етап 5. Подання критеріїв оцінювання якості ПЗ у вигляді полярних діаграм"));
        add(new H2("Комплексний показник якості ПЗ для експерту галузі"));
        add(horizontalLayouts.get(ExpertType.INDUSTRIAL));
        add(new H2("Комплексний показник якості ПЗ для експерту зручності користування"));
        add(horizontalLayouts.get(ExpertType.USABILITY));
        add(new H2("Комплексний показник якості ПЗ для експерту з програмування"));
        add(horizontalLayouts.get(ExpertType.PROGRAMMING));
        add(new H2("Комплексний показник якості ПЗ для потенційних користувачів"));
        add(horizontalLayouts.get(ExpertType.USERS));
        add(new H2("Комплексний показник якості ПЗ для узагальнених показників експертів"));
        add(horizontalLayouts.get(ExpertType.TOTAL));
    }
}
