package com.slee.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;

/**
 * Created by stephen on 8/20/15.
 */
@Configuration
public class BlogConfig {
    @Autowired
    DataSource dataSource;

    @Bean
    public JdbcTemplate getNamedParameterJdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }
}
