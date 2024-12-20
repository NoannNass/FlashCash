package com.app.flashcash.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/bootstrap.min.css", "/index.css/**", "/images/**", "/signin","/signup", "/").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin((form)-> form //page de connexion signin
                        .loginPage("/signin")
                        .permitAll().usernameParameter("email").defaultSuccessUrl("/dashboard", true) // email comme identifiant // redirection vers la page d'acceuil après une connexion
                )
                .logout((logout)-> logout.permitAll());

        return http.build();
    }

}
