package org.monjasa.application.views;

import java.util.Optional;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import org.monjasa.application.views.pages.quality.AttributeEvaluationsView;
import org.monjasa.application.views.pages.quality.QualityChartsView;
import org.monjasa.application.views.pages.risk.*;

@JsModule("./styles/shared-styles.js")
@CssImport("./styles/views/main/main-view.css")
@PWA(name = "Risk Management", shortName = "Risk Management", enableInstallPrompt = false)
public class MainView extends AppLayout {

    private final Tabs menu;

    private H1 viewTitle;

    public MainView() {
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        menu = createMenu();
        addToDrawer(createDrawerContent(menu));
    }

    private Component createHeaderContent() {

        HorizontalLayout layout = new HorizontalLayout();

        layout.setId("header");
        layout.getThemeList().set("dark", true);
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.add(new DrawerToggle());

        viewTitle = new H1();
        layout.add(viewTitle);

        return layout;
    }

    private Component createDrawerContent(Tabs menu) {

        VerticalLayout layout = new VerticalLayout();

        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getThemeList().set("spacing-s", true);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);

        HorizontalLayout logoLayout = new HorizontalLayout();

        logoLayout.setId("logo");
        logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        logoLayout.add(new Image("images/logo.png", "Управління ризиками - логотип"));
        logoLayout.add(new H1("Управління ризиками"));

        layout.add(logoLayout, menu);
        return layout;
    }

    private Tabs createMenu() {

        Tabs tabs = new Tabs();

        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        tabs.setId("tabs");
        tabs.add(createMenuItems());

        return tabs;
    }

    private Component[] createMenuItems() {
        return new Tab[]{
                createTab("Джерела ризиків", RiskSourcesView.class),
                createTab("Потенційні ризикові події", RiskEventsView.class),
                createTab("Ймовірність ризикових подій", RiskEventProbabilitiesView.class),
                createTab("Величина ризиків", RiskEventLossView.class),
                createTab("Планування ризиків", RiskArrangementsView.class),
                createTab("Оцінювання якості", AttributeEvaluationsView.class),
                createTab("Комплексні показники якості", QualityChartsView.class)
        };
    }

    private static Tab createTab(String text, Class<? extends Component> navigationTarget) {

        Tab tab = new Tab();
        tab.add(new RouterLink(text, navigationTarget));

        ComponentUtil.setData(tab, Class.class, navigationTarget);

        return tab;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        getTabForComponent(getContent()).ifPresent(menu::setSelectedTab);
        viewTitle.setText(getCurrentPageTitle());
    }

    private Optional<Tab> getTabForComponent(Component component) {
        return menu.getChildren().filter(tab -> ComponentUtil.getData(tab, Class.class).equals(component.getClass()))
                .findFirst().map(Tab.class::cast);
    }

    private String getCurrentPageTitle() {
        return getContent().getClass().getAnnotation(PageTitle.class).value();
    }
}
