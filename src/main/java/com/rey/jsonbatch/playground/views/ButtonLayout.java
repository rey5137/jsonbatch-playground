package com.rey.jsonbatch.playground.views;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ButtonLayout extends VerticalLayout {

    private Button upButton;
    private Button downButton;
    private Button addButton;
    private Button removeButton;

    public ButtonLayout() {
        upButton = new Button(new Icon(VaadinIcon.ARROW_UP));
        downButton = new Button(new Icon(VaadinIcon.ARROW_DOWN));
        addButton = new Button(new Icon(VaadinIcon.PLUS));
        removeButton = new Button(new Icon(VaadinIcon.MINUS));

        add(upButton, downButton, addButton, removeButton);
    }

    public void addOnUpButtonClick(ComponentEventListener<ClickEvent<Button>> listener) {
        upButton.addClickListener(listener);
    }

    public void addOnDownButtonClick(ComponentEventListener<ClickEvent<Button>> listener) {
        downButton.addClickListener(listener);
    }

    public void addOnAddButtonClick(ComponentEventListener<ClickEvent<Button>> listener) {
        addButton.addClickListener(listener);
    }

    public void addOnRemoveButtonClick(ComponentEventListener<ClickEvent<Button>> listener) {
        removeButton.addClickListener(listener);
    }

    public void setUpButtonEnabled(Boolean enabled) {
        upButton.setEnabled(enabled);
    }

    public void setDownButtonEnabled(Boolean enabled) {
        downButton.setEnabled(enabled);
    }

    public void setAddButtonEnabled(Boolean enabled) {
        addButton.setEnabled(enabled);
    }

    public void setRemoveButtonEnabled(Boolean enabled) {
        removeButton.setEnabled(enabled);
    }
}
