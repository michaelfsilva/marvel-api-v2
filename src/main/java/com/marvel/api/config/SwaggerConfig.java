package com.marvel.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
  @Value("${info.app.name}")
  private String appName;

  @Value("${info.app.description}")
  private String appDescription;

  @Value("${info.app.version}")
  private String appVersion;

  @Bean
  public Docket getApi() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.any())
        .build()
        .apiInfo(metaData())
        .securitySchemes(Collections.singletonList(getSecuritySchemes()))
        .securityContexts(Collections.singletonList(securityContext()));
  }

  private SecurityScheme getSecuritySchemes() {
    return new BasicAuth("basicAuth");
  }

  private SecurityContext securityContext() {
    return SecurityContext.builder().forPaths(regex("/*.*")).build();
  }

  private ApiInfo metaData() {
    return new ApiInfoBuilder()
        .title(this.appName)
        .description(this.appDescription)
        .version(this.appVersion)
        .contact(
            new Contact(
                "Michael Silva", "https://github.com/michaelfsilva", "michael.fsilva02@gmail.com"))
        .license("Apache License Version 2.0")
        .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
        .build();
  }
}
