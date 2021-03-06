package com.rey.jsonbatch.playground.views;

import com.rey.jsonbatch.playground.Utils;
import com.rey.jsonbatch.playground.model.ExtendedBatchTemplate;
import com.rey.jsonbatch.playground.model.ExtendedLoopTemplate;
import com.rey.jsonbatch.playground.model.ExtendedRequestTemplate;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The main view is a top-level placeholder for other views.
 */
@JsModule("./styles/shared-styles.js")
@PWA(name = "JsonBatch Playground", shortName = "JsonBatch Playground", enableInstallPrompt = false)
@Theme(value = Lumo.class, variant = Lumo.LIGHT)
@Route
@Push
public class MainView extends VerticalLayout implements TemplateChangeListener, HasUrlParameter<String>, LoopEditListener {

    private Logger logger = LoggerFactory.getLogger(MainView.class);

    TreeGrid<ExtendedRequestTemplate> requestGrid;
    ButtonLayout buttonLayout;
    TemplateLayout templateLayout;
    Button backButton;

    private List<ExtendedRequestTemplate> templateList = new ArrayList<>();

    public MainView() {
        setSizeFull();
        getStyle().set("overflow-y", "auto");

        H3 title = new H3("JsonBatch Playground");
        title.getStyle().set("margin-bottom", "0px");
        title.getStyle().set("margin-top", "0.5em");
        add(title);

        HorizontalLayout menuLayout = new HorizontalLayout();
        menuLayout.setPadding(false);
        menuLayout.setWidthFull();
        menuLayout.setSizeUndefined();
        add(menuLayout);

        Button sampleButton = new Button("Choose template");
        sampleButton.addClickListener(this::onSampleChoosingClicked);
        Button exportButton = new Button("Export/Import");
        exportButton.addClickListener(this::onExportChoosingClicked);
        menuLayout.add(sampleButton, exportButton,
                new Anchor("https://github.com/rey5137/jsonbatch", new Button("View JsonBatch")),
                new Anchor("https://github.com/rey5137/jsonbatch-playground", new Button("View source")));

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setSizeFull();
        mainLayout.setPadding(false);
        add(mainLayout);

        VerticalLayout leftLayout = new VerticalLayout();
        leftLayout.setHeightFull();
        leftLayout.setWidth("30%");
        leftLayout.setPadding(false);
        mainLayout.add(leftLayout);

        backButton = new Button(new Icon(VaadinIcon.ARROW_LEFT));
        backButton.setWidthFull();
        backButton.setIconAfterText(false);
        backButton.addClickListener(this::onBackClicked);
        leftLayout.add(backButton);

        requestGrid = new TreeGrid<>();
        requestGrid.setSizeFull();
        requestGrid.addHierarchyColumn(template -> Integer.toString(System.identityHashCode(template), 34).toUpperCase())
                .setHeader("Id")
                .setSortable(false)
                .setResizable(true);
        requestGrid.addColumn(ExtendedRequestTemplate::getLabel).setHeader("Title").setSortable(false);
        leftLayout.add(requestGrid);

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

        templateLayout = new TemplateLayout(this, this);
        templateLayout.setWidth("70%");
        mainLayout.add(templateLayout);

        requestGrid.asSingleSelect().addValueChangeListener(this::onRequestChanged);
        setBatchTemplate(new ExtendedBatchTemplate());
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        if (parameter != null) {
            String[] values = Utils.SAMPLES.values().toArray(new String[0]);
            switch (parameter) {
                case "sample1":
                    setBatchTemplate(Utils.loadFromPath(values[0]));
                    break;
                case "sample2":
                    setBatchTemplate(Utils.loadFromPath(values[1]));
                    break;
                case "sample3":
                    setBatchTemplate(Utils.loadFromPath(values[2]));
                    break;
            }
        }
    }

    @Override
    public void onTemplateChanged(ExtendedRequestTemplate requestTemplate) {
        requestGrid.getDataProvider().refreshItem(requestTemplate);
    }

    @Override
    public void onEditLoopRequests(ExtendedLoopTemplate loopTemplate) {
        setLoopTemplate(loopTemplate);
    }

    private void onRequestChanged(AbstractField.ComponentValueChangeEvent<Grid<ExtendedRequestTemplate>, ExtendedRequestTemplate> event) {
        ExtendedRequestTemplate currentTemplate = event.getValue();
        boolean showUpDown = currentTemplate != null && !(currentTemplate instanceof ExtendedBatchTemplate) && !(currentTemplate instanceof ExtendedLoopTemplate);
        templateLayout.setRequestTemplate(currentTemplate);
        buttonLayout.setUpButtonEnabled(showUpDown);
        buttonLayout.setDownButtonEnabled(showUpDown);
        buttonLayout.setAddButtonEnabled(currentTemplate != null);
        buttonLayout.setRemoveButtonEnabled(showUpDown);
    }

    private void onRequestUpClicked(ClickEvent<Button> event) {
        ExtendedRequestTemplate currentTemplate = templateLayout.getRequestTemplate();
        List<ExtendedRequestTemplate> templates = currentTemplate.getParent().getRequests();

        int index = templates.indexOf(currentTemplate);
        if (index == 0)
            return;

        templates.remove(currentTemplate);
        templates.add(index - 1, currentTemplate);

        for (int i = index - 1; i < templates.size(); i++)
            removeRequest(templates.get(i));

        for (int i = index - 1; i < templates.size(); i++)
            addRequest(templates.get(i), currentTemplate.getParent());

        requestGrid.getDataProvider().refreshAll();
    }

    private void onRequestDownClicked(ClickEvent<Button> event) {
        ExtendedRequestTemplate currentTemplate = templateLayout.getRequestTemplate();
        List<ExtendedRequestTemplate> templates = currentTemplate.getParent().getRequests();

        int index = templates.indexOf(currentTemplate);
        if (index == templates.size() - 1)
            return;

        templates.remove(currentTemplate);
        templates.add(index + 1, currentTemplate);

        for (int i = index; i < templates.size(); i++)
            removeRequest(templates.get(i));

        for (int i = index; i < templates.size(); i++)
            addRequest(templates.get(i), currentTemplate.getParent());

        requestGrid.getDataProvider().refreshAll();
    }

    private void onRequestAddClicked(ClickEvent<Button> event) {
        ExtendedRequestTemplate currentTemplate = templateLayout.getRequestTemplate();
        ExtendedRequestTemplate newTemplate = new ExtendedRequestTemplate();
        newTemplate.setParent(currentTemplate);
        newTemplate.setLoop(new ExtendedLoopTemplate());
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
        for (ExtendedRequestTemplate template : requestTemplate.getRequests())
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
        sampleBox.setItems(Utils.SAMPLES.keySet());
        sampleBox.setValue(Utils.SAMPLES.keySet().iterator().next());
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
            if (value != null && !value.isEmpty()) {
                setBatchTemplate(Utils.loadFromPath(Utils.SAMPLES.get(value)));
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
        textArea.setHeight("85%");
        textArea.setLabel("Batch template");
        textArea.setValue(Utils.toJson((ExtendedBatchTemplate) templateList.get(0)));
        verticalLayout.add(textArea);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        horizontalLayout.setHeight("15%");
        horizontalLayout.setJustifyContentMode(JustifyContentMode.END);
        verticalLayout.add(horizontalLayout);

        Button cancelButton = new Button("Cancel");
        Button importButton = new Button("Import");
        horizontalLayout.add(cancelButton, importButton);

        cancelButton.addClickListener(e -> dialog.close());
        importButton.addClickListener(e -> {
            setBatchTemplate(Utils.loadFromJson(textArea.getValue()));
            dialog.close();
        });
        dialog.open();
    }

    private void onBackClicked(ClickEvent<Button> event) {
        int size = templateList.size();
        if(size > 1) {
            templateList.remove(size - 1);
            ExtendedRequestTemplate template = templateList.get(size - 2);
            if(template instanceof ExtendedLoopTemplate) {
                backButton.setText("Back to " + templateList.get(size - 3).getLabel());
                backButton.setVisible(true);
                requestGrid.getTreeData().clear();
                addRequest(template, null);
                requestGrid.getDataProvider().refreshAll();
                requestGrid.select(template);
                requestGrid.expandRecursively(Collections.singletonList(template), 100);
                requestGrid.recalculateColumnWidths();
            }
            else
                setBatchTemplate((ExtendedBatchTemplate)template);
        }
    }

    private void setBatchTemplate(ExtendedBatchTemplate batchTemplate) {
        backButton.setVisible(false);
        templateList.clear();
        templateList.add(batchTemplate);
        requestGrid.getTreeData().clear();
        addRequest(batchTemplate, null);
        requestGrid.getDataProvider().refreshAll();
        requestGrid.select(batchTemplate);
        requestGrid.expandRecursively(Collections.singletonList(batchTemplate), 100);
        requestGrid.recalculateColumnWidths();
    }

    private void setLoopTemplate(ExtendedLoopTemplate loopTemplate) {
        backButton.setText("Back to " + templateList.get(templateList.size() - 1).getLabel());
        backButton.setVisible(true);
        templateList.add(loopTemplate);
        requestGrid.getTreeData().clear();
        addRequest(loopTemplate, null);
        requestGrid.getDataProvider().refreshAll();
        requestGrid.select(loopTemplate);
        requestGrid.expandRecursively(Collections.singletonList(loopTemplate), 100);
        requestGrid.recalculateColumnWidths();
    }

}
