package com.fiap.restaurant_management.config;

import com.fiap.restaurant_management.controllers.ApiPaths;
import com.fiap.restaurant_management.controllers.AuthController;
import com.fiap.restaurant_management.controllers.UsersController;
import com.fiap.restaurant_management.services.interfaces.AuthServiceContract;
import com.fiap.restaurant_management.services.interfaces.UsersServiceContract;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.configuration.SpringDocPageableConfiguration;
import org.springdoc.core.configuration.SpringDocSortConfiguration;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.webmvc.core.configuration.MultipleOpenApiSupportConfiguration;
import org.springdoc.webmvc.core.configuration.SpringDocWebMvcConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({
                AuthController.class,
                UsersController.class
})
@Import(OpenApiConfig.class)
@ImportAutoConfiguration({
                SpringDocConfiguration.class,
                SpringDocConfigProperties.class,
                SpringDocPageableConfiguration.class,
                SpringDocSortConfiguration.class,
                SpringDocWebMvcConfiguration.class,
                MultipleOpenApiSupportConfiguration.class
})
class OpenApiConfigTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private AuthServiceContract authService;

        @MockitoBean
        private UsersServiceContract usersService;

        @Test
        void shouldExposeV1OpenApiGroup() throws Exception {
                mockMvc.perform(get("/v3/api-docs/v1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.info.title").value("Restaurant Management API"))
                                .andExpect(jsonPath("$.info.version").value("v1"))
                                .andExpect(jsonPath("$.paths['" + ApiPaths.V1_AUTH_LOGIN + "']").value(notNullValue()))
                                .andExpect(jsonPath("$.paths['" + ApiPaths.V1_USERS + "']").value(notNullValue()));
        }
}
