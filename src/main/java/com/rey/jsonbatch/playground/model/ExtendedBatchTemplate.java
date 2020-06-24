package com.rey.jsonbatch.playground.model;

import com.rey.jsonbatch.model.DispatchOptions;
import com.rey.jsonbatch.model.LoopOptions;
import com.rey.jsonbatch.model.ResponseTemplate;

import java.util.List;

public class ExtendedBatchTemplate {

    private List<ExtendedRequestTemplate> requests;

    private List<ResponseTemplate> responses;

    private DispatchOptions dispatchOptions;

    private LoopOptions loopOptions;

    public List<ExtendedRequestTemplate> getRequests() {
        return requests;
    }

    public void setRequests(List<ExtendedRequestTemplate> requests) {
        this.requests = requests;
    }

    public List<ResponseTemplate> getResponses() {
        return responses;
    }

    public void setResponses(List<ResponseTemplate> responses) {
        this.responses = responses;
    }

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
}
