package com.rey.jsonbatch.playground.views;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rey.jsonbatch.playground.config.BatchConfiguration;
import com.rey.jsonbatch.playground.model.ExtendedBatchTemplate;
import com.rey.jsonbatch.playground.model.ExtendedRequestTemplate;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RequestDetailsLayout extends VerticalLayout {

    TextField titleField;
    TextField predicateField;

    VerticalLayout requestLayout;
    ComboBox<String> methodComboBox;
    TextField urlField;
    TextArea headersField;
    TextArea bodyField;

    VerticalLayout optionsLayout;
    Checkbox failBackAsStringBox;
    Checkbox ignoreParsingErrorBox;
    TextField maxLoopTimeField;


    private List<Registration> registrations = new ArrayList<>();

    private ExtendedRequestTemplate requestTemplate;
    private TemplateChangeListener templateChangeListener;

    private ObjectMapper objectMapper = BatchConfiguration.objectMapper();

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

        buildRequestLayout();
        buildOptionsLayout();
    }

    private void buildRequestLayout() {
        requestLayout = new VerticalLayout();
        requestLayout.setPadding(false);
        requestLayout.setSpacing(false);
        requestLayout.setSizeFull();
        add(requestLayout);

        predicateField = new TextField();
        predicateField.setLabel("Predicate");
        predicateField.setWidthFull();
        predicateField.setValueChangeMode(ValueChangeMode.LAZY);
        requestLayout.add(predicateField);

        HorizontalLayout urlLayout = new HorizontalLayout();
        urlLayout.setWidthFull();
        requestLayout.add(urlLayout);

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
        requestLayout.add(layout);

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

    private void buildOptionsLayout() {
        optionsLayout = new VerticalLayout();
        optionsLayout.setPadding(false);
        optionsLayout.setSpacing(false);
        optionsLayout.setSizeFull();
        add(optionsLayout);

        optionsLayout.add(new H4("Dispatch options"));

        HorizontalLayout dispatchLayout = new HorizontalLayout();
        dispatchLayout.setPadding(false);
        dispatchLayout.setWidthFull();
        optionsLayout.add(dispatchLayout);

        failBackAsStringBox = new Checkbox("Fail back as string");
        failBackAsStringBox.setWidth("50%");
        dispatchLayout.add(failBackAsStringBox);

        ignoreParsingErrorBox = new Checkbox("Ignore parsing error");
        ignoreParsingErrorBox.setWidth("50%");
        dispatchLayout.add(ignoreParsingErrorBox);

        optionsLayout.add(new H4("Loop options"));

        maxLoopTimeField = new TextField("Max loop time");
        maxLoopTimeField.setWidth("50%");
        maxLoopTimeField.setPattern("[0-9]*");
        maxLoopTimeField.setValueChangeMode(ValueChangeMode.LAZY);
        optionsLayout.add(maxLoopTimeField);

    }

    public void setRequestTemplate(ExtendedRequestTemplate requestTemplate) {
        this.requestTemplate = requestTemplate;
        registrations.forEach(Registration::remove);
        registrations.clear();

        if (this.requestTemplate != null) {
            if (this.requestTemplate instanceof ExtendedBatchTemplate) {
                ExtendedBatchTemplate batchTemplate = (ExtendedBatchTemplate) this.requestTemplate;
                titleField.setValue(Optional.ofNullable(batchTemplate.getTitle()).orElse(""));
                optionsLayout.setVisible(true);
                requestLayout.setVisible(false);
                failBackAsStringBox.setValue(batchTemplate.getDispatchOptions().getFailBackAsString());
                ignoreParsingErrorBox.setValue(batchTemplate.getDispatchOptions().getIgnoreParsingError());
                maxLoopTimeField.setValue(batchTemplate.getLoopOptions().getMaxLoopTime().toString());
                maxLoopTimeField.setInvalid(false);
                Collections.addAll(registrations,
                        titleField.addValueChangeListener(this::onTitleChanged),
                        failBackAsStringBox.addValueChangeListener(this::onFailBackAsStringChanged),
                        ignoreParsingErrorBox.addValueChangeListener(this::onIgnoreParsingErrorChanged),
                        maxLoopTimeField.addValueChangeListener(this::onMaxLoopTimeChanged)
                );
            } else {
                titleField.setValue(Optional.ofNullable(requestTemplate.getTitle()).orElse(""));
                optionsLayout.setVisible(false);
                requestLayout.setVisible(true);
                methodComboBox.setVisible(!requestTemplate.getUseLoop());
                urlField.setVisible(!requestTemplate.getUseLoop());
                headersField.setVisible(!requestTemplate.getUseLoop());
                bodyField.setVisible(!requestTemplate.getUseLoop());
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
    }

    private void onUrlChanged(AbstractField.ComponentValueChangeEvent<TextField, String> event) {
        requestTemplate.setUrl(event.getValue());
    }

    private void onHeadersChanged(AbstractField.ComponentValueChangeEvent<TextArea, String> event) {
        try {
            requestTemplate.setHeaders(objectMapper.readValue(event.getValue(), Object.class));
        } catch (JsonProcessingException e) {
        }
    }

    private void onBodyChanged(AbstractField.ComponentValueChangeEvent<TextArea, String> event) {
        try {
            requestTemplate.setBody(objectMapper.readValue(event.getValue(), Object.class));
        } catch (JsonProcessingException e) {
        }
    }

    private void onFailBackAsStringChanged(AbstractField.ComponentValueChangeEvent<Checkbox, Boolean> event) {
        ((ExtendedBatchTemplate) requestTemplate).getDispatchOptions().setFailBackAsString(event.getValue());
    }

    private void onIgnoreParsingErrorChanged(AbstractField.ComponentValueChangeEvent<Checkbox, Boolean> event) {
        ((ExtendedBatchTemplate) requestTemplate).getDispatchOptions().setIgnoreParsingError(event.getValue());
    }

    private void onMaxLoopTimeChanged(AbstractField.ComponentValueChangeEvent<TextField, String> event) {
        try {
            Integer value = Integer.parseInt(event.getValue());
            if (value >= 1 && value <= 1000) {
                ((ExtendedBatchTemplate) requestTemplate).getLoopOptions().setMaxLoopTime(value);
                maxLoopTimeField.setInvalid(false);
            } else
                maxLoopTimeField.setInvalid(true);
        } catch (Exception ex) {
            maxLoopTimeField.setInvalid(true);
        }
    }
}
