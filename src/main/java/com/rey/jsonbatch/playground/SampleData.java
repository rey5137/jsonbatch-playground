package com.rey.jsonbatch.playground;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rey.jsonbatch.playground.config.BatchConfiguration;
import com.rey.jsonbatch.playground.model.ExtendedBatchTemplate;
import com.rey.jsonbatch.playground.model.ExtendedRequestTemplate;

public class SampleData {

    private static ObjectMapper objectMapper = BatchConfiguration.buildObjectMapper();

    public static ExtendedBatchTemplate sample1() {
        String data = "{\n" +
                "    \"requests\": [\n" +
                "        {\n" +
                "            \"http_method\": \"GET\",\n" +
                "            \"url\": \"https://jsonplaceholder.typicode.com/posts\",\n" +
                "            \"headers\": {\n" +
                "                \"Accept\": \"str application/json, */*\"\n" +
                "            },\n" +
                "            \"body\": null,\n" +
                "            \"requests\": [\n" +
                "                {\n" +
                "                    \"http_method\": \"GET\",\n" +
                "                    \"url\": \"https://jsonplaceholder.typicode.com/posts/@{$.responses[0].body[0].id}@\",\n" +
                "                    \"headers\": {\n" +
                "                        \"Accept\": \"str application/json, */*\"\n" +
                "                    },\n" +
                "                    \"body\": null,\n" +
                "                    \"requests\": [\n" +
                "                        {\n" +
                "                            \"http_method\": \"POST\",\n" +
                "                            \"url\": \"https://jsonplaceholder.typicode.com/posts\",\n" +
                "                            \"headers\": {\n" +
                "                                \"Content-type\": \"str application/json; charset=UTF-8\"\n" +
                "                            },\n" +
                "                            \"body\": {\n" +
                "                                \"title\": \"str A new post\",\n" +
                "                                \"userId\": \"int $.responses[1].body.userId\",\n" +
                "                                \"body\": \"str $.responses[1].body.body\"\n" +
                "                            },\n" +
                "                            \"responses\": [\n" +
                "                                {\n" +
                "                                    \"predicate\": \"__cmp(\\\"@{$.responses[2].status}@ != 201\\\")\",\n" +
                "                                    \"status\": \"$.responses[2].status\",\n" +
                "                                    \"headers\": null,\n" +
                "                                    \"body\": {\n" +
                "                                        \"first_post\": \"obj $.responses[1].body\",\n" +
                "                                        \"new_post\": \"Error\"\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            ]\n" +
                "                        }\n" +
                "                    ]\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ],\n" +
                "    \"responses\": [\n" +
                "        {\n" +
                "            \"status\": \"$.responses[2].status\",\n" +
                "            \"headers\": null,\n" +
                "            \"body\": {\n" +
                "                \"first_post\": \"obj $.responses[1].body\",\n" +
                "                \"new_post\": \"obj $.responses[2].body\"\n" +
                "            }\n" +
                "        }\n" +
                "    ],\n" +
                "    \"dispatch_options\": {\n" +
                "        \"fail_back_as_string\": true,\n" +
                "        \"ignore_parsing_error\": true\n" +
                "    }\n" +
                "}";

        try {
            return (ExtendedBatchTemplate) cleanData(objectMapper.readValue(data, ExtendedBatchTemplate.class));
        } catch (JsonProcessingException e) {
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
