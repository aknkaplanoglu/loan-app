package com.akotako.loanservice.config;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Loan Service API")
                        .description("API documentation for the Loan Service application")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Akin Kaplanoglu")
                                .email("akn.kaplanoglu@gmail.com")
                                .url("https://google.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Project Documentation")
                        .url("https://google.com"));
    }

    @Bean
    public GlobalOpenApiCustomizer customizer() {
        return openApi -> openApi.getPaths().values().stream()
                .flatMap(pathItem -> pathItem.readOperations().stream())
                .filter(operation -> !operation.getOperationId().contains("register") && !operation.getOperationId().contains("login"))
                .forEach(operation ->
                        operation.addParametersItem(new HeaderParameter().name("Authorization")
                                .in(ParameterIn.HEADER.toString())
                                .required(true)));
    }
}
