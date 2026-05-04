package com.fiap.restaurant_management.config;

import com.fiap.restaurant_management.controllers.ApiPaths;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

        private static final String SECURITY_SCHEME_NAME = "jwt_auth";

        @Bean
        public GroupedOpenApi v1OpenApi() {
                return GroupedOpenApi.builder()
                                .group("v1")
                                .displayName("Restaurant Management API v1")
                                .pathsToMatch(ApiPaths.V1 + "/**")
                                .addOpenApiCustomizer(openApi -> {
                                        Components components = openApi.getComponents();
                                        if (components == null) {
                                                components = new Components();
                                                openApi.components(components);
                                        }

                                        components.addSecuritySchemes(SECURITY_SCHEME_NAME, securityScheme());
                                        openApi.addSecurityItem(
                                                        new SecurityRequirement().addList(SECURITY_SCHEME_NAME));

                                        openApi.info(new Info()
                                                        .title("Restaurant Management API")
                                                        .version("v1")
                                                        .description("API publica versionada por URI em /api/v1."))
                                                        .addServersItem(new Server()
                                                                        .url("/")
                                                                        .description("Current server"));
                                })
                                .build();
        }

        private SecurityScheme securityScheme() {
                return new SecurityScheme()
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .type(SecurityScheme.Type.HTTP);
        }
}
