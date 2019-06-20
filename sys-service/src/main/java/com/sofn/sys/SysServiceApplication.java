package com.sofn.sys;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Slf4j
@ComponentScan("com.sofn")
@SpringBootApplication
@EnableEurekaClient
public class SysServiceApplication {

	public static void main(String[] args) {
		Environment env = SpringApplication.run(SysServiceApplication.class, args).getEnvironment();
		log.info("\n----------------------------------------------------------\n\t" +
						"Application '{}' is running! \n----------------------------------------------------------",
				env.getProperty("spring.application.name"),
				env.getProperty("server.port")
		);
	}
}
