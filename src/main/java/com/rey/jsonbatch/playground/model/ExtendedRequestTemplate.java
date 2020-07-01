package com.rey.jsonbatch.playground.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.rey.jsonbatch.model.ResponseTemplate;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExtendedRequestTemplate {

    private String title;

    private Boolean useLoop = false;

    @JsonIgnore
    private ExtendedRequestTemplate parent;

    private String predicate;

    private String httpMethod;

    private String url;

    private Object headers;

    private Object body;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ExtendedRequestTemplate> requests = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ResponseTemplate> responses = new ArrayList<>();

    private ExtendedLoopTemplate loop;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ResponseTemplate> transformers = new ArrayList<>();

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getHeaders() {
        return headers;
    }

    public void setHeaders(Object headers) {
        this.headers = headers;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

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

    public Boolean getUseLoop() {
        return useLoop;
    }

    public void setUseLoop(Boolean useLoop) {
        this.useLoop = useLoop;
    }

    public ExtendedLoopTemplate getLoop() {
        return loop;
    }

    public void setLoop(ExtendedLoopTemplate loop) {
        this.loop = loop;
    }

    public List<ResponseTemplate> getTransformers() {
        return transformers;
    }

    public void setTransformers(List<ResponseTemplate> transformers) {
        this.transformers = transformers;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonIgnore
    public String getLabel() {
        if(title != null && !title.isEmpty())
            return title;
        if(predicate == null || predicate.isEmpty())
            return "Match All";
        if(predicate.length() <= 30)
            return predicate;
        return String.format("%s ...", predicate.substring(0, 30));
    }

    public ExtendedRequestTemplate getParent() {
        return parent;
    }

    public void setParent(ExtendedRequestTemplate parent) {
        this.parent = parent;
    }

}
