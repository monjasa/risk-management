package org.monjasa.application.views.pages.quality;

import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.monjasa.application.model.quality.AttributeType;
import org.monjasa.application.model.quality.Expert;
import org.monjasa.application.service.ExpertService;
import org.monjasa.application.views.MainView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Route(value = "attribute-evaluations", layout = MainView.class)
@PageTitle("Критерії оцінювання якості")
public class AttributeEvaluationsView extends VerticalLayout {

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    private ExpertService expertService;

    private final List<GridPro<Expert>> expertsGrids;

    @PostConstruct
    public void initializeDataProvider() {
        expertsGrids.forEach(expertGrid -> expertGrid.setDataProvider(expertService.getDataProviderFromCallbacks()));
    }

    public AttributeEvaluationsView() {

        setId("attribute-evaluations-view");

        int attributesCount = AttributeType.values().length;

        expertsGrids = List.of(
                setupGridForExperts(0, attributesCount / 2),
                setupGridForExperts(attributesCount / 2, attributesCount)
        );

        for (GridPro<Expert> expertsGrid : expertsGrids) {
            expertsGrid.addItemPropertyChangedListener(event -> expertService.save(event.getItem()));
        }

        add(new H1("Етап 4. Визначення критеріїв оцінювання якості ПЗ"));
        expertsGrids.forEach(this::add);
    }

    private GridPro<Expert> setupGridForExperts(int from, int to) {

        GridPro<Expert> expertsGrid = new GridPro<>();
        expertsGrid.setHeightByRows(true);
        expertsGrid.addThemeName("row-stripes");
        expertsGrid.addThemeName("wrap-cell-content");
        expertsGrid.getStyle().set("margin-bottom", "2em");

        expertsGrid.addColumn(Expert::getExpertType)
                .setSortProperty("name")
                .setFlexGrow(5)
                .setHeader("Експерт");
        expertsGrid.addEditColumn(Expert::getWeight)
                .text((item, newValue) -> item.setWeight(Integer.parseInt(newValue)))
                .setSortProperty("weight")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setHeader("Вагомість експерта");

        HeaderRow headerRow = expertsGrid.prependHeaderRow();

        AttributeType[] attributeTypes = AttributeType.values();
        for (AttributeType attributeType : Arrays.copyOfRange(attributeTypes, from, to)) {

            NumberRenderer<Expert> evaluationValueRenderer = new NumberRenderer<>(
                    expert -> expert.getEvaluationForAttribute(attributeType).getValue(),
                    "%(.2f",
                    Locale.US
            );

            Column<Expert> valueColumn = expertsGrid.addEditColumn(expert -> expert.getEvaluationForAttribute(attributeType).getValue(), evaluationValueRenderer)
                    .text((item, newValue) -> item.getEvaluationForAttribute(attributeType).setValue(Double.parseDouble(newValue)))
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setAutoWidth(true)
                    .setHeader("Оцінка");

            Column<Expert> weightColumn = expertsGrid.addEditColumn(expert -> expert.getEvaluationForAttribute(attributeType).getWeight())
                    .text((item, newValue) -> item.getEvaluationForAttribute(attributeType).setWeight(Integer.parseInt(newValue)))
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setAutoWidth(true)
                    .setHeader("Вагомість");

            headerRow.join(weightColumn, valueColumn)
                    .setText(attributeType.name);
        }

        return expertsGrid;
    }
}
