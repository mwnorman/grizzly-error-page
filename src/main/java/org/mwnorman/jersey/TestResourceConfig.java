package org.mwnorman.jersey;

import java.util.logging.Logger;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import com.fasterxml.jackson.jaxrs.base.JsonMappingExceptionMapper;
import com.fasterxml.jackson.jaxrs.base.JsonParseExceptionMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

public class TestResourceConfig extends ResourceConfig {
    
    public TestResourceConfig() {
        register(JacksonObjectMapperResolver.class);
        register(JacksonJaxbJsonProvider.class);
        register(JsonParseExceptionMapper.class);
        register(JsonMappingExceptionMapper.class);
        register(RolesAllowedDynamicFeature.class);
        packages("org.mwnorman.models;org.mwnorman.resources");
        registerInstances(new LoggingFilter(Logger.getLogger(TestResourceConfig.class.getName()), true));
    }
    
}