package com.rey.jsonbatch.playground.views;

import com.rey.jsonbatch.playground.model.ExtendedRequestTemplate;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

public class TemplateLayout extends VerticalLayout {

    VerticalLayout container;

    Tabs tabs;

    Tab detailsTab;
    Tab responsesTab;

    RequestDetailsLayout requestDetailsLayout;
    ResponseListLayout responseListLayout;

    private ExtendedRequestTemplate requestTemplate;

    public TemplateLayout(TemplateChangeListener templateChangeListener) {
        setSizeFull();
        setSpacing(false);
        setPadding(false);

        container = new VerticalLayout();
        container.setSizeFull();
        container.setSpacing(false);
        container.setPadding(false);
        add(container);

        detailsTab = new Tab("Details");
        responsesTab = new Tab("Responses");
        tabs = new Tabs(detailsTab, responsesTab);
        container.add(tabs);

        requestDetailsLayout = new RequestDetailsLayout(templateChangeListener);
        requestDetailsLayout.setSizeFull();
        requestDetailsLayout.setVisible(true);
        container.add(requestDetailsLayout);

        responseListLayout = new ResponseListLayout();
        responseListLayout.setSizeFull();
        responseListLayout.setVisible(false);
        container.add(responseListLayout);

        tabs.addSelectedChangeListener(event -> onTabSelectedChanged(tabs.getSelectedTab()));
        container.setVisible(false);
    }

    private void onTabSelectedChanged(Tab selectedTab) {
        requestDetailsLayout.setVisible(selectedTab == detailsTab);
        responseListLayout.setVisible(selectedTab == responsesTab);
    }

    public void setRequestTemplate(ExtendedRequestTemplate requestTemplate) {
        this.requestTemplate = requestTemplate;
        if(this.requestTemplate == null) {
            container.setVisible(false);
            requestDetailsLayout.setRequestTemplate(null);
        }
        else {
            container.setVisible(true);
            tabs.setSelectedTab(detailsTab);
            requestDetailsLayout.setRequestTemplate(requestTemplate);
        }
    }

    public ExtendedRequestTemplate getRequestTemplate() {
        return requestTemplate;
    }

}
