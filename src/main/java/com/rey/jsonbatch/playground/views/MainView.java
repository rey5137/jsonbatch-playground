package com.rey.jsonbatch.playground.views;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rey.jsonbatch.playground.SampleData;
import com.rey.jsonbatch.playground.model.ExtendedBatchTemplate;
import com.rey.jsonbatch.playground.model.ExtendedRequestTemplate;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The main view is a top-level placeholder for other views.
 */
@JsModule("./styles/shared-styles.js")
@PWA(name = "JsonBatch Playground", shortName = "JsonBatch Playground", enableInstallPrompt = false)
@Theme(value = Lumo.class, variant = Lumo.LIGHT)
@Route
@Push
public class MainView extends VerticalLayout implements TemplateChangeListener {

    TreeGrid<ExtendedRequestTemplate> requestGrid;
    ButtonLayout buttonLayout;
    TemplateLayout templateLayout;

    @Autowired
    public MainView(ObjectMapper objectMapper) {
        setSizeFull();

        H3 title = new H3("JsonBatch Playground");
        add(title);

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setSizeFull();
        mainLayout.setSpacing(false);
        add(mainLayout);

        requestGrid = new TreeGrid<>();
        requestGrid.setHeightFull();
        requestGrid.setWidth("30%");
        requestGrid.addHierarchyColumn(ExtendedRequestTemplate::getLabel).setHeader("Requests").setSortable(false);
        populate(requestGrid, objectMapper);
        mainLayout.add(requestGrid);

        buttonLayout = new ButtonLayout();
        buttonLayout.setSizeUndefined();
        mainLayout.add(buttonLayout);

        templateLayout = new TemplateLayout(this);
        templateLayout.setWidth("70%");
        mainLayout.add(templateLayout);

        requestGrid.asSingleSelect().addValueChangeListener(event -> templateLayout.setRequestTemplate(event.getValue()));
    }

    @Override
    public void onTemplateChanged(ExtendedRequestTemplate requestTemplate) {
        requestGrid.getDataProvider().refreshItem(requestTemplate);
    }

    private void populate(TreeGrid<ExtendedRequestTemplate> requestGrid, ObjectMapper objectMapper) {
        try {
            ExtendedBatchTemplate extendedBatchTemplate = SampleData.sample1(objectMapper);
            extendedBatchTemplate.getRequests().forEach(requestTemplate ->
                    add(requestGrid, requestTemplate, null));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void add(TreeGrid<ExtendedRequestTemplate> requestGrid, ExtendedRequestTemplate requestTemplate, ExtendedRequestTemplate parentTemplate) {
        requestGrid.getTreeData().addItem(parentTemplate, requestTemplate);
        requestTemplate.getRequests().forEach(child ->
                add(requestGrid, child, requestTemplate));
    }

}
