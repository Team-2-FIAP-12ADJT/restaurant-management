package com.fiap.restaurant_management.config;

import com.fiap.restaurant_management.controllers.ApiPaths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi v1OpenApi() {
        return GroupedOpenApi.builder()
                .group("v1")
                .displayName("Restaurant Management API v1")
                .pathsToMatch(ApiPaths.V1 + "/**")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("Restaurant Management API")
                                .version("v1")
                                .description("API publica versionada por URI em /api/v1."))
                        .addServersItem(new Server()
                                .url("/")
                                .description("Current server")))
                .build();
    }
}
