package com.gustavofelix.rest_spring_boot.serialization.converter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;

public final class YamlJackson2HttpMessageConverter
        extends AbstractJackson2HttpMessageConverter {

    public static final MediaType APPLICATION_YAML =
            MediaType.parseMediaType("application/x-yaml");

    public static final String MEDIA_TYPE_YAML = "application/x-yaml";

    public YamlJackson2HttpMessageConverter() {
        super(buildMapper(), APPLICATION_YAML);
    }

    private static YAMLMapper buildMapper() {
        YAMLMapper mapper = new YAMLMapper();

        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return mapper;
    }
}
