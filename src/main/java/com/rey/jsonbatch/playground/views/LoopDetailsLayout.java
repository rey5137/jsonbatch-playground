package com.rey.jsonbatch.playground.views;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rey.jsonbatch.playground.Utils;
import com.rey.jsonbatch.playground.config.BatchConfiguration;
import com.rey.jsonbatch.playground.model.ExtendedLoopTemplate;
import com.rey.jsonbatch.playground.model.ExtendedRequestTemplate;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class LoopDetailsLayout extends VerticalLayout {

    private Logger logger = LoggerFactory.getLogger(LoopDetailsLayout.class);

    Checkbox enableBox;
    TextField counterInitField;
    TextField counterPredicateField;
    TextField counterUpdateField;

    private ExtendedRequestTemplate requestTemplate;

    private List<Registration> registrations = new ArrayList<>();

    private ObjectMapper objectMapper = BatchConfiguration.objectMapper();

    private TemplateChangeListener templateChangeListener;

    public LoopDetailsLayout(TemplateChangeListener templateChangeListener) {
        this.templateChangeListener = templateChangeListener;
        setSizeFull();
        setSpacing(false);
        setPadding(false);

        enableBox = new Checkbox("Enable loop");
        add(enableBox);

        counterInitField = new TextField();
        counterInitField.setLabel("Counter Init");
        counterInitField.setWidthFull();
        counterInitField.setValueChangeMode(ValueChangeMode.LAZY);
        add(counterInitField);

        counterPredicateField = new TextField();
        counterPredicateField.setLabel("Counter Predicate");
        counterPredicateField.setWidthFull();
        counterPredicateField.setValueChangeMode(ValueChangeMode.LAZY);
        add(counterPredicateField);

        counterUpdateField = new TextField();
        counterUpdateField.setLabel("Counter Update");
        counterUpdateField.setWidthFull();
        counterUpdateField.setValueChangeMode(ValueChangeMode.LAZY);
        add(counterUpdateField);
    }

    public void setRequestTemplate(ExtendedRequestTemplate requestTemplate) {
        this.requestTemplate = requestTemplate;
        if (requestTemplate.getLoop() != null) {
            boolean enable = requestTemplate.getUseLoop();
            ExtendedLoopTemplate loopTemplate = requestTemplate.getLoop();
            enableBox.setValue(enable);
            counterInitField.setValue(Optional.ofNullable(loopTemplate.getCounterInit()).map(this::toJson).orElse(""));
            counterPredicateField.setValue(Optional.ofNullable(loopTemplate.getCounterPredicate()).map(this::toJson).orElse(""));
            counterUpdateField.setValue(Optional.ofNullable(loopTemplate.getCounterUpdate()).map(this::toJson).orElse(""));
            counterInitField.setVisible(enable);
            counterPredicateField.setVisible(enable);
            counterUpdateField.setVisible(enable);
            Collections.addAll(registrations,
                    enableBox.addValueChangeListener(this::onEnableChanged),
                    counterInitField.addValueChangeListener(this::onCounterInitChanged),
                    counterPredicateField.addValueChangeListener(this::onCounterPredicateChanged),
                    counterUpdateField.addValueChangeListener(this::onCounterUpdateChanged)
            );
        } else {
            registrations.forEach(Registration::remove);
            registrations.clear();
            enableBox.setValue(false);
            counterInitField.setValue("");
            counterPredicateField.setValue("");
            counterUpdateField.setValue("");
        }
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Error";
        }
    }

    private void onEnableChanged(AbstractField.ComponentValueChangeEvent<Checkbox, Boolean> event) {
        boolean enable = event.getValue();
        requestTemplate.setUseLoop(enable);
        counterInitField.setVisible(enable);
        counterPredicateField.setVisible(enable);
        counterUpdateField.setVisible(enable);
        templateChangeListener.onTemplateChanged(requestTemplate);
    }

    private void onCounterInitChanged(AbstractField.ComponentValueChangeEvent<TextField, String> event) {
        requestTemplate.getLoop().setCounterInit(Utils.parseData(event.getValue()));
    }

    private void onCounterPredicateChanged(AbstractField.ComponentValueChangeEvent<TextField, String> event) {
        requestTemplate.getLoop().setCounterPredicate(Utils.parseData(event.getValue()));
    }

    private void onCounterUpdateChanged(AbstractField.ComponentValueChangeEvent<TextField, String> event) {
        requestTemplate.getLoop().setCounterUpdate(Utils.parseData(event.getValue()));
    }

}
