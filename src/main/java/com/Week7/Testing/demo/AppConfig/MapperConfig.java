package com.Week7.Testing.demo.AppConfig;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public ModelMapper modelConfig() {
        return new ModelMapper();
    }
}
