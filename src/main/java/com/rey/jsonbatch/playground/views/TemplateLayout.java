package com.rey.jsonbatch.playground.views;

import com.rey.jsonbatch.playground.model.ExtendedBatchTemplate;
import com.rey.jsonbatch.playground.model.ExtendedRequestTemplate;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class TemplateLayout extends VerticalLayout {

    private Logger logger = LoggerFactory.getLogger(TemplateLayout.class);

    VerticalLayout container;

    Tabs tabs;

    Tab detailsTab;
    Tab responsesTab;
    Tab testingTab;

    RequestDetailsLayout requestDetailsLayout;
    ResponseListLayout responseListLayout;
    TestingLayout testingLayout;

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
        testingTab = new Tab("Testing");
        tabs = new Tabs(detailsTab, responsesTab, testingTab);
        container.add(tabs);

        requestDetailsLayout = new RequestDetailsLayout(templateChangeListener);
        requestDetailsLayout.setSizeFull();
        requestDetailsLayout.setVisible(true);
        container.add(requestDetailsLayout);

        responseListLayout = new ResponseListLayout();
        responseListLayout.setSizeFull();
        responseListLayout.setVisible(false);
        container.add(responseListLayout);

        testingLayout = new TestingLayout();
        testingLayout.setSizeFull();
        testingLayout.setVisible(false);
        container.add(testingLayout);

        tabs.addSelectedChangeListener(event -> onTabSelectedChanged(tabs.getSelectedTab()));
        container.setVisible(false);
    }

    private void onTabSelectedChanged(Tab selectedTab) {
        requestDetailsLayout.setVisible(selectedTab == detailsTab);
        responseListLayout.setVisible(selectedTab == responsesTab);
        testingLayout.setVisible(selectedTab == testingTab);
    }

    public void setRequestTemplate(ExtendedRequestTemplate requestTemplate) {
        this.requestTemplate = requestTemplate;

        if(requestTemplate == null) {
            container.setVisible(false);
            requestDetailsLayout.setRequestTemplate(null);
            responseListLayout.setResponseTemplates(Collections.emptyList());
            testingLayout.setBatchTemplate(null);
        }
        else {
            if(requestTemplate instanceof ExtendedBatchTemplate) {
                testingTab.setVisible(true);
                testingLayout.setBatchTemplate((ExtendedBatchTemplate)requestTemplate);
            }
            else
                testingTab.setVisible(false);
            container.setVisible(true);
            tabs.setSelectedTab(detailsTab);
            requestDetailsLayout.setRequestTemplate(requestTemplate);
            responseListLayout.setResponseTemplates(requestTemplate.getResponses());
        }
    }

    public ExtendedRequestTemplate getRequestTemplate() {
        return requestTemplate;
    }

}
