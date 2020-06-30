package com.rey.jsonbatch.playground;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rey.jsonbatch.model.ResponseTemplate;
import com.rey.jsonbatch.playground.config.BatchConfiguration;
import com.rey.jsonbatch.playground.model.ExtendedBatchTemplate;
import com.rey.jsonbatch.playground.model.ExtendedLoopTemplate;
import com.rey.jsonbatch.playground.model.ExtendedRequestTemplate;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class Utils {

    private static ObjectMapper objectMapper = BatchConfiguration.objectMapper();

    public static final LinkedHashMap<String, String> SAMPLES;

    static {
        SAMPLES = new LinkedHashMap<>();
        SAMPLES.put("Get & create post", "sample/first.json");
        SAMPLES.put("Search & group post by user", "sample/second.json");
    }

    public static ExtendedBatchTemplate loadFromPath(String path) {
        try {
            return (ExtendedBatchTemplate) cleanData(objectMapper.readValue(Utils.class.getClassLoader().getResourceAsStream(path), ExtendedBatchTemplate.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ExtendedBatchTemplate loadFromJson(String value) {
        try {
            return (ExtendedBatchTemplate) cleanData(objectMapper.readValue(value, ExtendedBatchTemplate.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ExtendedRequestTemplate cleanData(ExtendedRequestTemplate template) {
        for (ExtendedRequestTemplate child : template.getRequests()) {
            cleanData(child);
            child.setParent(template);
            if(template.getLoop() != null)
                template.setUseLoop(true);
        }
        return template;
    }

    public static String toJson(ExtendedBatchTemplate batchTemplate) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(cloneBatchTemplate(batchTemplate));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ExtendedBatchTemplate cloneBatchTemplate(ExtendedBatchTemplate template) {
        ExtendedBatchTemplate batchTemplate = new ExtendedBatchTemplate();
        batchTemplate.setTitle(template.getTitle());
        batchTemplate.setRequests(template.getRequests().stream().map(Utils::cloneRequestTemplate).collect(Collectors.toList()));
        batchTemplate.setResponses(template.getResponses().stream().map(Utils::cloneResponseTemplate).collect(Collectors.toList()));
        batchTemplate.setDispatchOptions(template.getDispatchOptions());
        batchTemplate.setLoopOptions(template.getLoopOptions());
        return batchTemplate;
    }

    private static ExtendedRequestTemplate cloneRequestTemplate(ExtendedRequestTemplate template) {
        ExtendedRequestTemplate requestTemplate = new ExtendedRequestTemplate();
        requestTemplate.setTitle(template.getTitle());
        if (template.getPredicate() != null && !template.getPredicate().isEmpty())
            requestTemplate.setPredicate(template.getPredicate());

        if(template.getUseLoop())
            requestTemplate.setLoop(cloneLoopTemplate(template.getLoop()));
        else {
            requestTemplate.setHttpMethod(template.getHttpMethod());
            requestTemplate.setUrl(template.getUrl());
            requestTemplate.setHeaders(template.getHeaders());
            requestTemplate.setBody(template.getBody());
            requestTemplate.setTransformers(template.getTransformers().stream().map(Utils::cloneResponseTemplate).collect(Collectors.toList()));
        }

        requestTemplate.setRequests(template.getRequests().stream().map(Utils::cloneRequestTemplate).collect(Collectors.toList()));
        requestTemplate.setResponses(template.getResponses().stream().map(Utils::cloneResponseTemplate).collect(Collectors.toList()));
        return requestTemplate;
    }

    private static ExtendedLoopTemplate cloneLoopTemplate(ExtendedLoopTemplate template) {
        if(template == null)
            return null;
        ExtendedLoopTemplate loopTemplate = new ExtendedLoopTemplate();
        loopTemplate.setCounterInit(template.getCounterInit());
        loopTemplate.setCounterPredicate(template.getCounterPredicate());
        loopTemplate.setCounterUpdate(template.getCounterUpdate());
        loopTemplate.setRequests(template.getRequests().stream()
                .map(Utils::cloneRequestTemplate)
                .collect(Collectors.toList()));
        return loopTemplate;
    }

    private static ResponseTemplate cloneResponseTemplate(ResponseTemplate template) {
        ResponseTemplate responseTemplate = new ResponseTemplate();
        if (template.getPredicate() != null && !template.getPredicate().isEmpty())
            responseTemplate.setPredicate(template.getPredicate());
        responseTemplate.setStatus(template.getStatus());
        responseTemplate.setHeaders(template.getHeaders());
        responseTemplate.setBody(template.getBody());
        return responseTemplate;
    }

}
