package com.rey.jsonbatch.playground.model;

import com.rey.jsonbatch.model.LoopTemplate;
import com.rey.jsonbatch.model.ResponseTemplate;

import java.util.List;

public class ExtendedRequestTemplate {

    private String title;

    private ExtendedRequestTemplate parent;

    private String predicate;

    private String httpMethod;

    private String url;

    private Object headers;

    private Object body;

    private List<ExtendedRequestTemplate> requests;

    private List<ResponseTemplate> responses;

    private LoopTemplate loop;

    private List<ResponseTemplate> transformers;

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

    public LoopTemplate getLoop() {
        return loop;
    }

    public void setLoop(LoopTemplate loop) {
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
