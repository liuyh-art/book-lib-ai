package com.example.book.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {
    @Bean
    public Counter borrowCounter(MeterRegistry registry) {
        return Counter.builder("book_borrow_total").description("Total number of book borrows").tag("type", "borrow").register(registry);
    }
    @Bean
    public Counter returnCounter(MeterRegistry registry) {
        return Counter.builder("book_return_total").description("Total number of book returns").tag("type", "return").register(registry);
    }
}
