package com.sofn.gatewayservice;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.sofn.gatewayservice.auth.AuthFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.jdbc.DataSourceHealthIndicatorAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@EnableZuulProxy
@ComponentScan("com.sofn")
@SpringBootApplication(exclude={
        DataSourceAutoConfiguration.class,
        DataSourceHealthIndicatorAutoConfiguration.class,
        DruidDataSourceAutoConfigure.class
})
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }

    @Bean
    public AuthFilter preRequestLogFilter() {
        return new AuthFilter();
    }
}
