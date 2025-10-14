package com.sistema.productos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir.productos}")
    private String uploadDirProductos;

    @Value("${file.upload-dir.categorias}")
    private String uploadDirCategorias;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/productos/**")
                .addResourceLocations("file:" + uploadDirProductos);
        registry.addResourceHandler("/categorias/**")
                .addResourceLocations("file:" + uploadDirCategorias);
    }
}