package org.mwnorman.jersey;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

@Provider
public class JacksonObjectMapperResolver implements ContextResolver<ObjectMapper>  {

	static ObjectMapper objectMapper = new ObjectMapper()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
        .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        .setAnnotationIntrospector(new JaxbAnnotationIntrospector(TypeFactory.defaultInstance()));
	
	public JacksonObjectMapperResolver() {
	}

	public static ObjectMapper getObjectMapper() {
        return objectMapper;
	}

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return objectMapper;
    }
	
}