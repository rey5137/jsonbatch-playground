package com.rey.jsonbatch.playground.views;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rey.jsonbatch.BatchEngine;
import com.rey.jsonbatch.model.BatchTemplate;
import com.rey.jsonbatch.model.LoopTemplate;
import com.rey.jsonbatch.model.Request;
import com.rey.jsonbatch.model.RequestTemplate;
import com.rey.jsonbatch.model.ResponseTemplate;
import com.rey.jsonbatch.playground.config.BatchConfiguration;
import com.rey.jsonbatch.playground.model.ExtendedBatchTemplate;
import com.rey.jsonbatch.playground.model.ExtendedLoopTemplate;
import com.rey.jsonbatch.playground.model.ExtendedRequestTemplate;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.Command;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class TestingLayout extends HorizontalLayout {

    ComboBox<String> methodComboBox;
    TextField urlField;
    TextArea headersField;
    TextArea bodyField;

    TextArea responseField;
    Button runButton;

    private UI ui;

    private ObjectMapper objectMapper = BatchConfiguration.objectMapper();

    private ExtendedBatchTemplate batchTemplate;

    private ExecutorService executorService = Executors.newCachedThreadPool();
    private BatchEngine batchEngine = BatchConfiguration.batchEngine();

    public TestingLayout() {
        setSizeFull();
        setSpacing(true);
        setPadding(false);

        VerticalLayout originalLayout = new VerticalLayout();
        originalLayout.setHeightFull();
        originalLayout.setWidth("50%");
        originalLayout.setSpacing(false);
        originalLayout.setPadding(false);
        add(originalLayout);

        HorizontalLayout urlLayout = new HorizontalLayout();
        urlLayout.setWidthFull();
        originalLayout.add(urlLayout);

        methodComboBox = new ComboBox<>();
        methodComboBox.setItems("GET", "POST", "PUT", "PATCH", "DELETE");
        methodComboBox.setLabel("Method");
        methodComboBox.setWidth("30%");
        urlLayout.add(methodComboBox);

        urlField = new TextField();
        urlField.setLabel("Url");
        urlField.setWidth("70%");
        urlLayout.add(urlField);

        headersField = new TextArea();
        headersField.setLabel("Headers");
        headersField.setWidthFull();
        headersField.setMaxHeight("300px");
        originalLayout.add(headersField);

        bodyField = new TextArea();
        bodyField.setLabel("Body");
        bodyField.setWidthFull();
        bodyField.setMaxHeight("300px");
        originalLayout.add(bodyField);
        originalLayout.setFlexGrow(1f, headersField, bodyField);

        VerticalLayout responseLayout = new VerticalLayout();
        responseLayout.setHeightFull();
        responseLayout.setWidth("50%");
        responseLayout.setSpacing(false);
        responseLayout.setPadding(false);
        add(responseLayout);

        runButton = new Button("Execute batch");
        responseLayout.add(runButton);

        responseField = new TextArea();
        responseField.setWidthFull();
        responseField.setMaxHeight("600px");
        responseField.setLabel("Response");
        responseField.setReadOnly(true);
        responseLayout.add(responseField);
        responseLayout.setFlexGrow(1f, responseField);

        runButton.addClickListener(this::onRunClicked);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        this.ui = attachEvent.getUI();
    }

    public void setBatchTemplate(ExtendedBatchTemplate batchTemplate) {
        this.batchTemplate = batchTemplate;
    }

    private void onRunClicked(ClickEvent<Button> event) {
        Request originalRequest = buildOriginalRequest();
        BatchTemplate batchTemplate = buildBatchTemplate(this.batchTemplate);
        runButton.setEnabled(false);
        responseField.setValue("Executing ...");
        executorService.submit(() -> {
            Object response;
            try {
                response = batchEngine.execute(originalRequest, batchTemplate);
            } catch (Exception e) {
                ui.access((Command) () -> {
                    runButton.setEnabled(true);
                    responseField.setValue("Error when executing request");
                });
                return;
            }

            try {
                final String value = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
                ui.access((Command) () -> {
                    runButton.setEnabled(true);
                    responseField.setValue(value);
                });
            } catch (JsonProcessingException e) {
                ui.access((Command) () -> {
                    responseField.setEnabled(true);
                    responseField.setValue("Error when parsing response");
                });
            }
        });

    }

    private BatchTemplate buildBatchTemplate(ExtendedBatchTemplate template) {
        BatchTemplate batchTemplate = new BatchTemplate();
        batchTemplate.setRequests(template.getRequests().stream().map(this::buildRequestTemplate).collect(Collectors.toList()));
        batchTemplate.setResponses(template.getResponses().stream().map(this::cleanData).collect(Collectors.toList()));
        batchTemplate.setDispatchOptions(template.getDispatchOptions());
        batchTemplate.setLoopOptions(template.getLoopOptions());
        return batchTemplate;
    }

    private RequestTemplate buildRequestTemplate(ExtendedRequestTemplate template) {
        RequestTemplate requestTemplate = new RequestTemplate();
        if (template.getPredicate() != null && !template.getPredicate().isEmpty())
            requestTemplate.setPredicate(template.getPredicate());

        if(template.getUseLoop())
            requestTemplate.setLoop(buildLoopTemplate(template.getLoop()));
        else {
            requestTemplate.setHttpMethod(template.getHttpMethod());
            requestTemplate.setUrl(template.getUrl());
            requestTemplate.setHeaders(template.getHeaders());
            requestTemplate.setBody(template.getBody());
            requestTemplate.setTransformers(template.getTransformers().stream().map(this::cleanData).collect(Collectors.toList()));
        }

        requestTemplate.setRequests(template.getRequests().stream().map(this::buildRequestTemplate).collect(Collectors.toList()));
        requestTemplate.setResponses(template.getResponses().stream().map(this::cleanData).collect(Collectors.toList()));
        return requestTemplate;
    }

    private LoopTemplate buildLoopTemplate(ExtendedLoopTemplate template) {
        if(template == null)
            return null;
        LoopTemplate loopTemplate = new LoopTemplate();
        loopTemplate.setCounterInit(template.getCounterInit());
        loopTemplate.setCounterPredicate(template.getCounterPredicate());
        loopTemplate.setCounterUpdate(template.getCounterUpdate());
        loopTemplate.setRequests(template.getRequests().stream()
                .map(this::buildRequestTemplate)
                .collect(Collectors.toList()));
        return loopTemplate;
    }

    private Request buildOriginalRequest() {
        Request request = new Request();
        request.setHttpMethod(methodComboBox.getValue());
        request.setUrl(urlField.getValue());
        try {
            request.setHeaders(objectMapper.readValue(headersField.getValue(), Map.class));
        } catch (Exception e) {
        }
        try {
            request.setBody(objectMapper.readValue(bodyField.getValue(), Object.class));
        } catch (Exception e) {
        }
        return request;
    }

    private ResponseTemplate cleanData(ResponseTemplate template) {
        if (template.getPredicate() != null && template.getPredicate().isEmpty())
            template.setPredicate(null);
        return template;
    }

}
