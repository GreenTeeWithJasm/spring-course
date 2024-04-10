package com.sasha.course.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Set password encoder
    @Bean
    public static BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    // Disable default "ROLE_" role prefix
    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(configurer -> {
            configurer.requestMatchers(HttpMethod.POST, "/api/login").permitAll();
            configurer.requestMatchers(HttpMethod.GET, "/students").hasAnyRole("STUDENT", "TEACHER", "ADMIN");
            configurer.requestMatchers(HttpMethod.GET, "/students/**").hasAnyRole("TEACHER", "ADMIN");
            configurer.requestMatchers(HttpMethod.POST, "/students").hasAnyRole("TEACHER", "ADMIN");
            configurer.requestMatchers(HttpMethod.PUT, "/students/**").hasAnyRole("TEACHER", "ADMIN");
            configurer.requestMatchers(HttpMethod.DELETE, "/students/**").hasAnyRole("ADMIN");
        });

        httpSecurity.httpBasic(Customizer.withDefaults());

        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }
}
