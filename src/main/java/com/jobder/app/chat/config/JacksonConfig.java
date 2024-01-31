package com.jobder.app.chat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.SimpleDateFormat;
import java.util.List;

@Configuration
@EnableWebMvc
public class JacksonConfig implements WebMvcConfigurer {

    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(dateMappingJacksonHttpMessageConverter());
    }

    private MappingJackson2HttpMessageConverter dateMappingJacksonHttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
        converter.setObjectMapper(objectMapper);
        return converter;
    }
}

