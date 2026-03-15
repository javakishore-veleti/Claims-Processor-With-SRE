package com.healthcare.claims.portal.sre.config;

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
        registry.addViewController("/service-health").setViewName("forward:/index.html");
        registry.addViewController("/incidents").setViewName("forward:/index.html");
        registry.addViewController("/deployments").setViewName("forward:/index.html");
        registry.addViewController("/cloud-resources").setViewName("forward:/index.html");
        registry.addViewController("/tenant-analytics").setViewName("forward:/index.html");
        registry.addViewController("/slo-compliance").setViewName("forward:/index.html");
        registry.addViewController("/cost-tracking").setViewName("forward:/index.html");
    }
}
