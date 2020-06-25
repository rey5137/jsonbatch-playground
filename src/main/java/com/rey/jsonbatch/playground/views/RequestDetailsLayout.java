package com.rey.jsonbatch.playground.views;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rey.jsonbatch.playground.config.BatchConfiguration;
import com.rey.jsonbatch.playground.model.ExtendedBatchTemplate;
import com.rey.jsonbatch.playground.model.ExtendedRequestTemplate;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RequestDetailsLayout extends VerticalLayout {

    TextField titleField;
    TextField predicateField;
    ComboBox<String> methodComboBox;
    TextField urlField;
    TextArea headersField;
    TextArea bodyField;

    private List<Registration> registrations = new ArrayList<>();

    private ExtendedRequestTemplate requestTemplate;
    private TemplateChangeListener templateChangeListener;

    private ObjectMapper objectMapper = BatchConfiguration.buildObjectMapper();

    public RequestDetailsLayout(TemplateChangeListener templateChangeListener) {
        this.templateChangeListener = templateChangeListener;

        setSizeFull();
        setSpacing(false);
        setPadding(false);

        titleField = new TextField();
        titleField.setLabel("Title");
        titleField.setWidthFull();
        titleField.setValueChangeMode(ValueChangeMode.LAZY);
        add(titleField);

        predicateField = new TextField();
        predicateField.setLabel("Predicate");
        predicateField.setWidthFull();
        predicateField.setValueChangeMode(ValueChangeMode.LAZY);
        add(predicateField);

        HorizontalLayout urlLayout = new HorizontalLayout();
        urlLayout.setWidthFull();
        add(urlLayout);

        methodComboBox = new ComboBox<>();
        methodComboBox.setItems("GET", "POST", "PUT", "PATCH", "DELETE");
        methodComboBox.setLabel("Method");
        methodComboBox.setWidth("20%");
        urlLayout.add(methodComboBox);

        urlField = new TextField();
        urlField.setLabel("Url");
        urlField.setWidth("80%");
        urlField.setValueChangeMode(ValueChangeMode.LAZY);
        urlLayout.add(urlField);

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
    }

    public void setRequestTemplate(ExtendedRequestTemplate requestTemplate) {
        this.requestTemplate = requestTemplate;
        if (this.requestTemplate != null) {
            if(this.requestTemplate instanceof ExtendedBatchTemplate) {
                titleField.setValue(Optional.ofNullable(requestTemplate.getTitle()).orElse(""));
                predicateField.setVisible(false);
                methodComboBox.setVisible(false);
                urlField.setVisible(false);
                headersField.setVisible(false);
                bodyField.setVisible(false);
                Collections.addAll(registrations,
                        titleField.addValueChangeListener(this::onTitleChanged)
                );
            }
            else {
                titleField.setValue(Optional.ofNullable(requestTemplate.getTitle()).orElse(""));
                predicateField.setVisible(true);
                methodComboBox.setVisible(true);
                urlField.setVisible(true);
                headersField.setVisible(true);
                bodyField.setVisible(true);
                predicateField.setValue(Optional.ofNullable(requestTemplate.getPredicate()).orElse(""));
                methodComboBox.setValue(Optional.ofNullable(requestTemplate.getHttpMethod()).orElse("").toUpperCase());
                urlField.setValue(Optional.ofNullable(requestTemplate.getUrl()).orElse(""));
                headersField.setValue(Optional.ofNullable(requestTemplate.getHeaders()).map(headers -> {
                    try {
                        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(headers);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        return "Error";
                    }
                }).orElse(""));
                bodyField.setValue(Optional.ofNullable(requestTemplate.getBody()).map(body -> {
                    try {
                        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        return "Error";
                    }
                }).orElse(""));

                Collections.addAll(registrations,
                        titleField.addValueChangeListener(this::onTitleChanged),
                        predicateField.addValueChangeListener(this::onPredicateChanged),
                        methodComboBox.addValueChangeListener(this::onMethodChanged),
                        urlField.addValueChangeListener(this::onUrlChanged),
                        headersField.addValueChangeListener(this::onHeadersChanged),
                        bodyField.addValueChangeListener(this::onBodyChanged)
                );
            }
        } else {
            registrations.forEach(Registration::remove);
            registrations.clear();
            titleField.setValue("");
            predicateField.setValue("");
            urlField.setValue("");
            headersField.setValue("");
            bodyField.setValue("");
        }
    }

    private void onTitleChanged(AbstractField.ComponentValueChangeEvent<TextField, String> event) {
        requestTemplate.setTitle(event.getValue());
        templateChangeListener.onTemplateChanged(requestTemplate);
    }

    private void onPredicateChanged(AbstractField.ComponentValueChangeEvent<TextField, String> event) {
        requestTemplate.setPredicate(event.getValue());
        templateChangeListener.onTemplateChanged(requestTemplate);
    }

    private void onMethodChanged(AbstractField.ComponentValueChangeEvent<ComboBox<String>, String> event) {
        requestTemplate.setHttpMethod(event.getValue());
        templateChangeListener.onTemplateChanged(requestTemplate);
    }

    private void onUrlChanged(AbstractField.ComponentValueChangeEvent<TextField, String> event) {
        requestTemplate.setUrl(event.getValue());
        templateChangeListener.onTemplateChanged(requestTemplate);
    }

    private void onHeadersChanged(AbstractField.ComponentValueChangeEvent<TextArea, String> event) {
        try {
            requestTemplate.setHeaders(objectMapper.readValue(event.getValue(), Object.class));
            templateChangeListener.onTemplateChanged(requestTemplate);
        } catch (JsonProcessingException e) {}
    }

    private void onBodyChanged(AbstractField.ComponentValueChangeEvent<TextArea, String> event) {
        try {
            requestTemplate.setBody(objectMapper.readValue(event.getValue(), Object.class));
            templateChangeListener.onTemplateChanged(requestTemplate);
        } catch (JsonProcessingException e) {}
    }
}
