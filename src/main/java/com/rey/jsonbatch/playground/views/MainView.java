package com.rey.jsonbatch.playground.views;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rey.jsonbatch.playground.SampleData;
import com.rey.jsonbatch.playground.config.BatchConfiguration;
import com.rey.jsonbatch.playground.model.ExtendedBatchTemplate;
import com.rey.jsonbatch.playground.model.ExtendedRequestTemplate;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextArea;
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

    private Logger logger = LoggerFactory.getLogger(MainView.class);

    TreeGrid<ExtendedRequestTemplate> requestGrid;
    ButtonLayout buttonLayout;
    TemplateLayout templateLayout;

    private ObjectMapper objectMapper = BatchConfiguration.objectMapper();
    private ExtendedBatchTemplate batchTemplate;

    public MainView() {
        setSizeFull();

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setSizeUndefined();
        titleLayout.setPadding(false);

        add(titleLayout);
        H3 title = new H3("JsonBatch Playground");
        titleLayout.add(title);

        Button sampleButton = new Button("Choose sample template");
        sampleButton.addClickListener(this::onSampleChoosingClicked);
        titleLayout.add(sampleButton);
        titleLayout.setVerticalComponentAlignment(Alignment.END, sampleButton);

        Button exportButton = new Button("Export/Import template");
        exportButton.addClickListener(this::onExportChoosingClicked);
        titleLayout.add(exportButton);
        titleLayout.setVerticalComponentAlignment(Alignment.END, exportButton);

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
        batchTemplate = new ExtendedBatchTemplate();
        addRequest(batchTemplate, null);
        requestGrid.select(batchTemplate);
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

    private void onSampleChoosingClicked(ClickEvent<Button> event) {
        Dialog dialog = new Dialog();
        dialog.setWidth("30%");

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setPadding(false);
        dialog.add(verticalLayout);

        final ComboBox<String> sampleBox = new ComboBox<>();
        sampleBox.setWidthFull();
        sampleBox.setLabel("Batch template");
        sampleBox.setItems(SampleData.SAMPLES.keySet());
        sampleBox.setValue(SampleData.SAMPLES.keySet().iterator().next());
        verticalLayout.add(sampleBox);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        horizontalLayout.setJustifyContentMode(JustifyContentMode.END);
        verticalLayout.add(horizontalLayout);

        Button cancelButton = new Button("Cancel");
        Button chooseButton = new Button("Choose");
        horizontalLayout.add(cancelButton, chooseButton);

        cancelButton.addClickListener(e -> dialog.close());
        chooseButton.addClickListener(e -> {
            String value = sampleBox.getValue();
            if(value != null && !value.isEmpty()) {
                setBatchTemplate(SampleData.load(SampleData.SAMPLES.get(value)));
            }
            dialog.close();
        });
        dialog.open();
    }

    private void onExportChoosingClicked(ClickEvent<Button> event) {
        Dialog dialog = new Dialog();
        dialog.setWidth("80%");
        dialog.setHeight("80%");

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setPadding(false);
        dialog.add(verticalLayout);

        final TextArea textArea = new TextArea();
        textArea.setWidthFull();
        textArea.setHeight("80%");
        textArea.setLabel("Batch template");
        verticalLayout.add(textArea);

        try {
            textArea.setValue(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(batchTemplate));
        } catch (Exception e) {
            e.printStackTrace();
        }

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        horizontalLayout.setHeight("20%");
        horizontalLayout.setJustifyContentMode(JustifyContentMode.END);
        verticalLayout.add(horizontalLayout);

        Button cancelButton = new Button("Cancel");
        Button importButton = new Button("Import");
        horizontalLayout.add(cancelButton, importButton);

        cancelButton.addClickListener(e -> dialog.close());
        importButton.addClickListener(e -> {
            try {
                setBatchTemplate(objectMapper.readValue(textArea.getValue(), ExtendedBatchTemplate.class));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            dialog.close();
        });
        dialog.open();
    }

    private void setBatchTemplate(ExtendedBatchTemplate batchTemplate) {
        this.batchTemplate = batchTemplate;
        requestGrid.getTreeData().clear();
        addRequest(batchTemplate, null);
        requestGrid.getDataProvider().refreshAll();
        requestGrid.select(batchTemplate);
    }
}
