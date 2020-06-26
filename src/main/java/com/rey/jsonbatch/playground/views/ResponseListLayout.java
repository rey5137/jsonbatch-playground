package com.rey.jsonbatch.playground.views;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rey.jsonbatch.model.ResponseTemplate;
import com.rey.jsonbatch.playground.config.BatchConfiguration;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ResponseListLayout extends HorizontalLayout {

    private Logger logger = LoggerFactory.getLogger(ResponseListLayout.class);

    Grid<ResponseTemplate> responseGrid;
    ButtonLayout buttonLayout;

    private ObjectMapper objectMapper = BatchConfiguration.objectMapper();

    private List<ResponseTemplate> responseTemplates;

    private ResponseTemplate currentTemplate;

    public ResponseListLayout() {
        setSizeFull();
        setPadding(false);

        responseGrid = new Grid<>();
        responseGrid.addColumn(ResponseTemplate::getPredicate).setHeader("Predicate").setWidth("20%").setResizable(true);
        responseGrid.addColumn(ResponseTemplate::getStatus).setHeader("Status").setWidth("10%").setResizable(true);
        responseGrid.addColumn(responseTemplate -> {
            try {
                return responseTemplate.getHeaders() == null ? "" : objectMapper.writeValueAsString(responseTemplate.getHeaders());
            } catch (JsonProcessingException e) {
                return "Error";
            }
        }).setHeader("Headers").setWidth("35%").setResizable(true);
        responseGrid.addColumn(responseTemplate -> {
            try {
                return responseTemplate.getBody() == null ? "" : objectMapper.writeValueAsString(responseTemplate.getBody());
            } catch (JsonProcessingException e) {
                return "Error";
            }
        }).setHeader("Body").setWidth("35%").setResizable(true);
        responseGrid.setSizeFull();
        add(responseGrid);

        buttonLayout = new ButtonLayout(true);
        buttonLayout.setSizeUndefined();
        buttonLayout.addOnUpButtonClick(this::onResponseUpClicked);
        buttonLayout.addOnDownButtonClick(this::onResponseDownClicked);
        buttonLayout.addOnAddButtonClick(this::onResponseAddClicked);
        buttonLayout.addOnEditButtonClick(this::onResponseEditClicked);
        buttonLayout.addOnRemoveButtonClick(this::onResponseRemoveClicked);
        buttonLayout.setUpButtonEnabled(false);
        buttonLayout.setDownButtonEnabled(false);
        buttonLayout.setAddButtonEnabled(true);
        buttonLayout.setEditButtonEnabled(false);
        buttonLayout.setRemoveButtonEnabled(false);
        add(buttonLayout);

        responseGrid.asSingleSelect().addValueChangeListener(this::onResponseChanged);
    }

    public void setResponseTemplates(List<ResponseTemplate> responseTemplates) {
        this.responseTemplates = responseTemplates;
        responseGrid.setItems(responseTemplates);
    }

    private void onResponseChanged(AbstractField.ComponentValueChangeEvent<Grid<ResponseTemplate>, ResponseTemplate> event) {
        currentTemplate = event.getValue();
        buttonLayout.setUpButtonEnabled(currentTemplate != null);
        buttonLayout.setDownButtonEnabled(currentTemplate != null);
        buttonLayout.setEditButtonEnabled(currentTemplate != null);
        buttonLayout.setRemoveButtonEnabled(currentTemplate != null);
    }

    private void onResponseUpClicked(ClickEvent<Button> event) {
        int index = responseTemplates.indexOf(currentTemplate);
        if(index > 0) {
            responseTemplates.remove(currentTemplate);
            responseTemplates.add(index - 1, currentTemplate);
            responseGrid.setItems(responseTemplates);
            responseGrid.getDataProvider().refreshAll();
            responseGrid.select(currentTemplate);
        }
    }

    private void onResponseDownClicked(ClickEvent<Button> event) {
        int index = responseTemplates.indexOf(currentTemplate);
        if(index < responseTemplates.size() - 1) {
            responseTemplates.remove(currentTemplate);
            responseTemplates.add(index + 1, currentTemplate);
            responseGrid.setItems(responseTemplates);
            responseGrid.getDataProvider().refreshAll();
            responseGrid.select(currentTemplate);
        }
    }

    private void onResponseAddClicked(ClickEvent<Button> event) {
        showDetailsDialog(new ResponseTemplate(), true);
    }

    private void onResponseEditClicked(ClickEvent<Button> event) {
        showDetailsDialog(currentTemplate, false);
    }

    private void onResponseRemoveClicked(ClickEvent<Button> event) {
        responseTemplates.remove(currentTemplate);
        responseGrid.setItems(responseTemplates);
        responseGrid.getDataProvider().refreshAll();
    }

    private void showDetailsDialog(final ResponseTemplate responseTemplate, boolean isNew) {
        Dialog dialog = new Dialog();
        dialog.setWidth("80%");
        dialog.setHeight("80%");
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setPadding(false);
        dialog.add(verticalLayout);

        ResponseDetailsLayout responseDetailsLayout = new ResponseDetailsLayout(responseTemplate);
        verticalLayout.add(responseDetailsLayout);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        horizontalLayout.setJustifyContentMode(JustifyContentMode.END);
        verticalLayout.add(horizontalLayout);

        Button cancelButton = new Button("Cancel");
        Button saveButton = new Button("Save");
        horizontalLayout.add(cancelButton, saveButton);

        cancelButton.addClickListener(event -> dialog.close());
        saveButton.addClickListener(event -> {
            responseTemplate.setPredicate(responseDetailsLayout.getPredicate());
            responseTemplate.setStatus(responseDetailsLayout.getStatus());
            responseTemplate.setHeaders(responseDetailsLayout.getHeaders());
            responseTemplate.setBody(responseDetailsLayout.getBody());

            if (isNew) {
                responseTemplates.add(responseTemplate);
                responseGrid.setItems(responseTemplates);
            }

            responseGrid.getDataProvider().refreshAll();
            dialog.close();
        });
        dialog.open();
    }

}
