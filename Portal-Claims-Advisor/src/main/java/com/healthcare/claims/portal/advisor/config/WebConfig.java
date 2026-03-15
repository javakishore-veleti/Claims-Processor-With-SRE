package com.healthcare.claims.portal.advisor.config;

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
        // Forward root and Angular routes to index.html
        registry.addViewController("/").setViewName("forward:/index.html");
        registry.addViewController("/dashboard").setViewName("forward:/index.html");
        registry.addViewController("/claims").setViewName("forward:/index.html");
        registry.addViewController("/intake").setViewName("forward:/index.html");
        registry.addViewController("/members").setViewName("forward:/index.html");
        registry.addViewController("/adjudication").setViewName("forward:/index.html");
        registry.addViewController("/reports").setViewName("forward:/index.html");
        registry.addViewController("/audit").setViewName("forward:/index.html");
    }
}
