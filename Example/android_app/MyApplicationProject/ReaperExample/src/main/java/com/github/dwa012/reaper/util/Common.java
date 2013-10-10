package com.github.dwa012.reaper.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dwa012.reaper.wrapper.GenericResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class Common {
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static JsonFactory jsonFactory = new MappingJsonFactory();

    // Database date format
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public static ObjectMapper getObjectMapper() {
        objectMapper.setDateFormat(Common.DATE_FORMATTER);
        objectMapper.getDeserializationConfig().with(Common.DATE_FORMATTER);
        return objectMapper;
    }

    public static JsonFactory getJsonFactory() {
        return jsonFactory;
    }

    public static GenericResponse parseJsonResponse(String json) {
        GenericResponse result = new GenericResponse();
        try {
            result =  Common.getObjectMapper().readValue(json,GenericResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
