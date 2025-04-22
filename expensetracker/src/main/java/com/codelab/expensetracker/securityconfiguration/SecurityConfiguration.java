package com.codelab.expensetracker.securityconfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {


        httpSecurity.csrf(csrf -> csrf
                .disable()
        );

        httpSecurity
                // Authorization configuration
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/**").hasRole("USER")  // Only 'USER' role can access /user/**
                        .requestMatchers("/admin/**").hasRole("ADMIN")  // Only 'ADMIN' role can access /admin/**
                        .anyRequest().permitAll()  // Allow all other requests
                )

                // Login configuration (no deprecated methods)
                .formLogin(form -> form
                        .loginPage("/login")  // Custom login page
                        .loginProcessingUrl("/dologin")  // URL for handling the form submission--> will be same as the parameter used in th:action in login form
                        .successHandler(customAuthenticationSuccessHandler)
                )

                // Logout configuration (no deprecated methods)
                .logout(logout -> logout
                        .logoutUrl("/logout")  // Logout URL
                        .logoutSuccessUrl("/login?logout=true")  // Redirect to login page after logout
                        .invalidateHttpSession(true)  // Invalidate the session on logout
                        .clearAuthentication(true)  // Clear authentication on logout
                );

        return httpSecurity.build();
    }



    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        System.out.println("step1");
        return new BCryptPasswordEncoder();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        System.out.println("step2");
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        System.out.println("step3");
        return new CustomUserDetailsService();
    }



}
