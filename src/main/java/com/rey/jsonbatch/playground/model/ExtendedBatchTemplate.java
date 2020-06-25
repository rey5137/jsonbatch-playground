package com.rey.jsonbatch.playground.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.rey.jsonbatch.model.DispatchOptions;
import com.rey.jsonbatch.model.LoopOptions;

@JsonIgnoreProperties({"loop", "predicate", "httpMethod", "url", "headers", "body", "transformers"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExtendedBatchTemplate extends ExtendedRequestTemplate {

    private DispatchOptions dispatchOptions;

    private LoopOptions loopOptions;

    public DispatchOptions getDispatchOptions() {
        return dispatchOptions;
    }

    public void setDispatchOptions(DispatchOptions dispatchOptions) {
        this.dispatchOptions = dispatchOptions;
    }

    public LoopOptions getLoopOptions() {
        return loopOptions;
    }

    public void setLoopOptions(LoopOptions loopOptions) {
        this.loopOptions = loopOptions;
    }

    @Override
    public String getLabel() {
        if(getTitle() != null && !getTitle().isEmpty())
            return getTitle();
        return "Batch template";
    }
}
