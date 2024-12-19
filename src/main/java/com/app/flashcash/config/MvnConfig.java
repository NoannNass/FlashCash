package com.app.flashcash.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvnConfig implements WebMvcConfigurer {

    public void addViewControllers(ViewControllerRegistry registry) { // addViewControllers permet de: créer un controlleur simple pour le chemin et associé ce chemin à une vue
        registry.addViewController("/signin").setViewName("signin");
//        registry.addViewController("/friends").setViewName("/friends");
    }

}
