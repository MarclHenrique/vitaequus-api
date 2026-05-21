package com.reproequinos.vitaequus_api.config;

import com.reproequinos.vitaequus_api.auth.AuthController;
import com.reproequinos.vitaequus_api.controllers.AnimalController;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class OpenApiConfig {

    public static final String JWT_SECURITY_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI vitaEquusOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("VitaEquus API")
                        .description("API para gestao reprodutiva, clinica e operacional de equinos.")
                        .version("v1"))
                .components(new Components()
                        .addSecuritySchemes(JWT_SECURITY_SCHEME, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(JWT_SECURITY_SCHEME));
    }

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    public GlobalOpenApiCustomizer globalDocumentationCustomizer() {
        return this::customizeGeneratedOpenApi;
    }

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    public OpenApiCustomizer publicAuthEndpointsCustomizer() {
        return this::customizeGeneratedOpenApi;
    }

    private void customizeGeneratedOpenApi(OpenAPI openApi) {
        openApi.getPaths().forEach((path, pathItem) -> {
            if (path.startsWith("/auth/")) {
                pathItem.readOperations().forEach(this::removeSecurityRequirement);
            }
            if ("/api/v1/animais/{id}/foto".equals(path) && pathItem.getPost() != null) {
                configureAnimalPhotoUpload(pathItem.getPost());
            }
        });
    }

    @Bean
    public OperationCustomizer operationDocumentationCustomizer() {
        return (operation, handlerMethod) -> {
            Class<?> beanType = handlerMethod.getBeanType();

            if (AuthController.class.equals(beanType)) {
                removeSecurityRequirement(operation);
            }

            if (AnimalController.class.equals(beanType) && "upload".equals(handlerMethod.getMethod().getName())) {
                configureAnimalPhotoUpload(operation);
            }

            return operation;
        };
    }

    private void removeSecurityRequirement(Operation operation) {
        operation.setSecurity(Collections.emptyList());
    }

    private void configureAnimalPhotoUpload(Operation operation) {
        Schema<?> fileSchema = new Schema<>()
                .type("string")
                .format("binary")
                .description("Arquivo da foto");

        Schema<?> uploadSchema = new Schema<>()
                .type("object")
                .addProperty("file", fileSchema)
                .addRequiredItem("file");

        operation.setRequestBody(new RequestBody()
                .required(true)
                .content(new Content()
                        .addMediaType("multipart/form-data", new MediaType()
                                .schema(uploadSchema))));
    }
}
