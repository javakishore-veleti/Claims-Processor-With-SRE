package com.healthcare.claims.portal.member.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/browser/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
        registry.addViewController("/dashboard").setViewName("forward:/index.html");
        registry.addViewController("/my-claims").setViewName("forward:/index.html");
        registry.addViewController("/claims").setViewName("forward:/index.html");
        registry.addViewController("/documents").setViewName("forward:/index.html");
        registry.addViewController("/eob").setViewName("forward:/index.html");
        registry.addViewController("/appeal").setViewName("forward:/index.html");
        registry.addViewController("/profile").setViewName("forward:/index.html");
    }
}
