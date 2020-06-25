package com.rey.jsonbatch.playground.views;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rey.jsonbatch.model.ResponseTemplate;
import com.rey.jsonbatch.playground.config.BatchConfiguration;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.Optional;

public class ResponseDetailsLayout extends VerticalLayout {

    TextField predicateField;
    TextField statusField;
    TextArea headersField;
    TextArea bodyField;

    private ObjectMapper objectMapper = BatchConfiguration.buildObjectMapper();

    public ResponseDetailsLayout(ResponseTemplate responseTemplate) {
        setSizeFull();
        setSpacing(false);
        setPadding(false);

        predicateField = new TextField();
        predicateField.setLabel("Predicate");
        predicateField.setWidthFull();
        predicateField.setValueChangeMode(ValueChangeMode.LAZY);
        add(predicateField);

        statusField = new TextField();
        statusField.setLabel("Status");
        statusField.setWidthFull();
        statusField.setValueChangeMode(ValueChangeMode.LAZY);
        add(statusField);

        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.setMaxHeight("60%");
        add(layout);

        headersField = new TextArea();
        headersField.setLabel("Headers");
        headersField.setHeightFull();
        headersField.setWidth("40%");
        headersField.setValueChangeMode(ValueChangeMode.LAZY);
        layout.add(headersField);

        bodyField = new TextArea();
        bodyField.setLabel("Body");
        bodyField.setHeightFull();
        bodyField.setWidth("60%");
        bodyField.setValueChangeMode(ValueChangeMode.LAZY);
        layout.add(bodyField);

        setResponseTemplate(responseTemplate);
    }

    private void setResponseTemplate(ResponseTemplate responseTemplate) {
        predicateField.setVisible(true);
        statusField.setVisible(true);
        headersField.setVisible(true);
        bodyField.setVisible(true);
        predicateField.setValue(Optional.ofNullable(responseTemplate.getPredicate()).orElse(""));
        statusField.setValue(Optional.ofNullable(responseTemplate.getStatus()).orElse(""));
        headersField.setValue(Optional.ofNullable(responseTemplate.getHeaders()).map(headers -> {
            try {
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(headers);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return "Error";
            }
        }).orElse(""));
        bodyField.setValue(Optional.ofNullable(responseTemplate.getBody()).map(body -> {
            try {
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return "Error";
            }
        }).orElse(""));
    }

    public String getPredicate() {
        return predicateField.getValue();
    }

    public String getStatus() {
        return statusField.getValue();
    }

    public Object getHeaders() {
        try {
            return objectMapper.readValue(headersField.getValue(), Object.class);
        } catch (JsonProcessingException e) {
        }
        return null;
    }

    public Object getBody() {
        try {
            return objectMapper.readValue(bodyField.getValue(), Object.class);
        } catch (JsonProcessingException e) {
        }
        return null;
    }
}
