package com.vbz.dbcards.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class JacksonConfig {
	
	@Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        // Spring Boot's auto-configured ObjectMapper disables this by default.
        // Since we override the @Bean, we must explicitly disable it ourselves —
        // otherwise Jackson throws UnrecognizedPropertyException (→ 500) when
        // the mobile client sends field names that don't exactly match the DTO.
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }

}
