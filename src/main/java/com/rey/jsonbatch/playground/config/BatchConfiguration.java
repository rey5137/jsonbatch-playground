package com.rey.jsonbatch.playground.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.rey.jsonbatch.BatchEngine;
import com.rey.jsonbatch.JsonBuilder;
import com.rey.jsonbatch.RequestDispatcher;
import com.rey.jsonbatch.apachehttpclient.ApacheHttpClientRequestDispatcher;
import com.rey.jsonbatch.function.BeanShellFunction;
import com.rey.jsonbatch.function.Function;
import com.rey.jsonbatch.function.Functions;
import com.rey.jsonbatch.function.GroovyFunction;
import org.apache.http.impl.client.HttpClients;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BatchConfiguration {

    public static ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        return objectMapper;
    }

    public static BatchEngine batchEngine() {
        ObjectMapper objectMapper = objectMapper();
        com.jayway.jsonpath.Configuration conf = com.jayway.jsonpath.Configuration.builder()
                .options(Option.SUPPRESS_EXCEPTIONS)
                .jsonProvider(new JacksonJsonProvider(objectMapper))
                .mappingProvider(new JacksonMappingProvider(objectMapper))
                .build();

        List<Function> functions = new ArrayList<>();
        Collections.addAll(functions, Functions.basic());
        Collections.addAll(functions, new BeanShellFunction(), new GroovyFunction());
        JsonBuilder jsonBuilder = new JsonBuilder(functions.toArray(new Function[0]));

        RequestDispatcher requestDispatcher = new ApacheHttpClientRequestDispatcher(HttpClients.createDefault());

        return new BatchEngine(conf, jsonBuilder, requestDispatcher);
    }
}
