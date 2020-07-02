package com.rey.jsonbatch.playground.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties({"loop", "predicate", "httpMethod", "url", "headers", "body", "transformers", "responses"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExtendedLoopTemplate extends ExtendedRequestTemplate {

    private Object counterInit;

    private Object counterPredicate;

    private Object counterUpdate;

    public Object getCounterInit() {
        return counterInit;
    }

    public void setCounterInit(Object counterInit) {
        this.counterInit = counterInit;
    }

    public Object getCounterPredicate() {
        return counterPredicate;
    }

    public void setCounterPredicate(Object counterPredicate) {
        this.counterPredicate = counterPredicate;
    }

    public Object getCounterUpdate() {
        return counterUpdate;
    }

    public void setCounterUpdate(Object counterUpdate) {
        this.counterUpdate = counterUpdate;
    }

    @Override
    public String getLabel() {
        return String.format("Loop: %s", getParent().getLabel());
    }
}
