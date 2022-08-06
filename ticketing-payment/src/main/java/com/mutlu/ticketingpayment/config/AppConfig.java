package com.mutlu.ticketingpayment.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class AppConfig {
    @Bean
    public JdbcTemplate jdbcTemplate(HikariDataSource hikariDataSource) {
        JdbcTemplate template = new JdbcTemplate(hikariDataSource);
        template.execute("""
                create table if not exists payment
                (
                    payment_id   serial
                        constraint payments_pk
                            primary key,
                    user_id      int,
                    trip_id     int,
                    payment_type varchar(20),
                    card_number varchar(40),
                    payment_date timestamp
                );
                """);
        return template;
    }
}