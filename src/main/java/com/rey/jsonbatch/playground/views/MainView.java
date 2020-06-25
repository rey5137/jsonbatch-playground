package com.rey.jsonbatch.playground.views;

import com.rey.jsonbatch.playground.SampleData;
import com.rey.jsonbatch.playground.model.ExtendedBatchTemplate;
import com.rey.jsonbatch.playground.model.ExtendedRequestTemplate;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * The main view is a top-level placeholder for other views.
 */
@JsModule("./styles/shared-styles.js")
@PWA(name = "JsonBatch Playground", shortName = "JsonBatch Playground", enableInstallPrompt = false)
@Theme(value = Lumo.class, variant = Lumo.LIGHT)
@Route
@Push
public class MainView extends VerticalLayout implements TemplateChangeListener {

    private UI ui;

    private Logger logger = LoggerFactory.getLogger(MainView.class);

    TreeGrid<ExtendedRequestTemplate> requestGrid;
    ButtonLayout buttonLayout;
    TemplateLayout templateLayout;

    public MainView() {
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
        requestGrid.addHierarchyColumn(template -> Integer.toString(System.identityHashCode(template), 34).toUpperCase()).setHeader("Id").setSortable(false);
        requestGrid.addColumn(ExtendedRequestTemplate::getLabel).setHeader("Title").setSortable(false);
        mainLayout.add(requestGrid);

        buttonLayout = new ButtonLayout(false);
        buttonLayout.setSizeUndefined();
        buttonLayout.addOnUpButtonClick(this::onRequestUpClicked);
        buttonLayout.addOnDownButtonClick(this::onRequestDownClicked);
        buttonLayout.addOnAddButtonClick(this::onRequestAddClicked);
        buttonLayout.addOnRemoveButtonClick(this::onRequestRemoveClicked);
        buttonLayout.setUpButtonEnabled(false);
        buttonLayout.setDownButtonEnabled(false);
        buttonLayout.setAddButtonEnabled(false);
        buttonLayout.setRemoveButtonEnabled(false);
        mainLayout.add(buttonLayout);

        templateLayout = new TemplateLayout(this);
        templateLayout.setWidth("70%");
        mainLayout.add(templateLayout);

        requestGrid.asSingleSelect().addValueChangeListener(this::onRequestChanged);

        addRequest(SampleData.sample1(), null);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        this.ui = attachEvent.getUI();
    }

    @Override
    public void onTemplateChanged(ExtendedRequestTemplate requestTemplate) {
        requestGrid.getDataProvider().refreshItem(requestTemplate);
    }

    private void onRequestChanged(AbstractField.ComponentValueChangeEvent<Grid<ExtendedRequestTemplate>, ExtendedRequestTemplate> event) {
        ExtendedRequestTemplate currentTemplate = event.getValue();
        templateLayout.setRequestTemplate(currentTemplate);
        buttonLayout.setUpButtonEnabled(currentTemplate != null && !(currentTemplate instanceof ExtendedBatchTemplate));
        buttonLayout.setDownButtonEnabled(currentTemplate != null && !(currentTemplate instanceof ExtendedBatchTemplate));
        buttonLayout.setAddButtonEnabled(currentTemplate != null);
        buttonLayout.setRemoveButtonEnabled(currentTemplate != null && !(currentTemplate instanceof ExtendedBatchTemplate));
    }

    private void onRequestUpClicked(ClickEvent<Button> event) {
        ExtendedRequestTemplate currentTemplate = templateLayout.getRequestTemplate();
        List<ExtendedRequestTemplate> templates = currentTemplate.getParent().getRequests();

        int index = templates.indexOf(currentTemplate);
        if(index == 0)
            return;

        templates.remove(currentTemplate);
        templates.add(index - 1, currentTemplate);

        for(int i = index - 1; i < templates.size(); i++)
            removeRequest(templates.get(i));

        for(int i = index - 1; i < templates.size(); i++)
            addRequest(templates.get(i), currentTemplate.getParent());

        requestGrid.getDataProvider().refreshAll();
    }

    private void onRequestDownClicked(ClickEvent<Button> event) {
        ExtendedRequestTemplate currentTemplate = templateLayout.getRequestTemplate();
        List<ExtendedRequestTemplate> templates = currentTemplate.getParent().getRequests();

        int index = templates.indexOf(currentTemplate);
        if(index == templates.size() - 1)
            return;

        templates.remove(currentTemplate);
        templates.add(index + 1, currentTemplate);

        for(int i = index; i < templates.size(); i++)
            removeRequest(templates.get(i));

        for(int i = index; i < templates.size(); i++)
            addRequest(templates.get(i), currentTemplate.getParent());

        requestGrid.getDataProvider().refreshAll();
    }

    private void onRequestAddClicked(ClickEvent<Button> event) {
        ExtendedRequestTemplate currentTemplate = templateLayout.getRequestTemplate();
        ExtendedRequestTemplate newTemplate = new ExtendedRequestTemplate();
        newTemplate.setParent(currentTemplate);
        currentTemplate.getRequests().add(newTemplate);

        requestGrid.getTreeData().addItem(currentTemplate, newTemplate);
        requestGrid.getDataProvider().refreshItem(currentTemplate, true);
    }

    private void onRequestRemoveClicked(ClickEvent<Button> event) {
        ExtendedRequestTemplate currentTemplate = templateLayout.getRequestTemplate();
        currentTemplate.getParent().getRequests().remove(currentTemplate);

        removeRequest(currentTemplate);
        requestGrid.getDataProvider().refreshAll();
    }

    private void removeRequest(ExtendedRequestTemplate requestTemplate) {
        for(ExtendedRequestTemplate template : requestTemplate.getRequests())
            removeRequest(template);

        requestGrid.getTreeData().removeItem(requestTemplate);
    }

    private void addRequest(ExtendedRequestTemplate requestTemplate, ExtendedRequestTemplate parentTemplate) {
        requestGrid.getTreeData().addItem(parentTemplate, requestTemplate);
        requestTemplate.getRequests().forEach(child ->
                addRequest(child, requestTemplate));
    }

}
