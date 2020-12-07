package com.example.application.views.about;

import com.example.application.views.main.MainView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route(value = "about", layout = MainView.class)
@PageTitle("About")
public class AboutView extends Div {

    public AboutView() {
        setId("about-view");

        GridPro<String> grid = new GridPro<>();
        grid.setItems(List.of("Hello", "Bye", "Vaadin"));

        grid.addEditColumn(String::valueOf, "name")
                .text((item, newValue) ->
                        item = newValue)
                .setHeader("Name (editable)");

        add(grid);
    }
}
