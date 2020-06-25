package com.rey.jsonbatch.playground;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rey.jsonbatch.playground.config.BatchConfiguration;
import com.rey.jsonbatch.playground.model.ExtendedBatchTemplate;
import com.rey.jsonbatch.playground.model.ExtendedRequestTemplate;

import java.util.LinkedHashMap;

public class SampleData {

    private static ObjectMapper objectMapper = BatchConfiguration.objectMapper();

    public static final LinkedHashMap<String, String> SAMPLES;

    static {
        SAMPLES = new LinkedHashMap<>();
        SAMPLES.put("Get & create post", "sample/first.json");
        SAMPLES.put("Search & group post by user", "sample/second.json");
    }

    public static ExtendedBatchTemplate load(String path) {
        try {
            return (ExtendedBatchTemplate) cleanData(objectMapper.readValue(SampleData.class.getClassLoader().getResourceAsStream(path), ExtendedBatchTemplate.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ExtendedRequestTemplate cleanData(ExtendedRequestTemplate template) {
        for (ExtendedRequestTemplate child : template.getRequests()) {
            cleanData(child);
            child.setParent(template);
        }
        return template;
    }
}
