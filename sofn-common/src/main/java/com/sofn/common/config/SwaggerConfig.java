package com.sofn.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Locale;


/**
 *
 * Swagger 配置类
 */
@EnableSwagger2
@Configuration
public class SwaggerConfig {

    @Value(value = "${swagger.base-package:com.sofn}")
    String basePackage;
    @Value(value = "${swagger.name:}")
    String name;
    @Value(value = "${swagger.prefix:}")
    String prefix;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(false)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        String title = String.format(Locale.getDefault(),"%s[以下接口需在URL前加%s前缀]",name,prefix);
        String desc = String.format(Locale.getDefault(),"%s Restful API",name);
        return new ApiInfoBuilder()
                .title(title)
                .description(desc)
                .version("1.0")
                .build();
    }
}
