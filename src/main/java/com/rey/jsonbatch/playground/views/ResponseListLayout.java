package com.rey.jsonbatch.playground.views;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rey.jsonbatch.model.ResponseTemplate;
import com.rey.jsonbatch.playground.config.BatchConfiguration;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class ResponseListLayout extends HorizontalLayout {

    Grid<ResponseTemplate> responseGrid;

    private ObjectMapper objectMapper = BatchConfiguration.buildObjectMapper();

    public ResponseListLayout() {
        setSizeFull();
        setSpacing(false);
        setPadding(false);

        responseGrid = new Grid<>();
        responseGrid.addColumn(ResponseTemplate::getPredicate).setHeader("Predicate");
        responseGrid.addColumn(ResponseTemplate::getStatus).setHeader("Status");
        responseGrid.addColumn(responseTemplate -> {
            try {
                return responseTemplate.getBody() == null ? "" : objectMapper.writeValueAsString(responseTemplate.getBody());
            } catch (JsonProcessingException e) {
                return "Error";
            }
        }).setHeader("Body");
        responseGrid.setSizeFull();
        add(responseGrid);

        ButtonLayout buttonLayout = new ButtonLayout();
        buttonLayout.setSizeUndefined();
        add(buttonLayout);
    }

}
