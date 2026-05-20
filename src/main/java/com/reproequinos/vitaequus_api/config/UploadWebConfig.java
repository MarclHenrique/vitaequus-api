package com.reproequinos.vitaequus_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class UploadWebConfig implements WebMvcConfigurer {

    @Value("${app.upload.base-dir:uploads}")
    private String uploadBaseDir;

    @Value("${app.upload.public-path:/uploads}")
    private String uploadPublicPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadBaseDir).toAbsolutePath().normalize();

        registry.addResourceHandler(normalizarPublicPath() + "/**")
                .addResourceLocations(uploadPath.toUri().toString());
    }

    private String normalizarPublicPath() {
        String publicPath = uploadPublicPath == null || uploadPublicPath.isBlank()
                ? "/uploads"
                : uploadPublicPath.trim().replace("\\", "/");

        if (!publicPath.startsWith("/")) {
            publicPath = "/" + publicPath;
        }

        return publicPath.replaceAll("/+$", "");
    }
}
