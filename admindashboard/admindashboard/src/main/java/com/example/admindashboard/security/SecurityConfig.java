package com.example.admindashboard.security;

import com.example.admindashboard.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // NEW: This turns on @PreAuthorize!
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // DelegatingPasswordEncoder supports BOTH:
        // - {noop}welcome123  (existing plaintext users)
        // - {bcrypt}$2a$...   (new BCrypt-encoded users)
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Note: In production, consider enabling CSRF for form endpoints
                .authorizeHttpRequests(auth -> auth
                        // Publicly accessible assets and login
                        .requestMatchers("/login", "/forgot-password", "/api/forgot-password", "/reset-password", "/api/reset-password", "/my-thanks/login", "/my-thanks/authenticate", "/my-rides/login", "/my-rides/logout", "/api/verify-user", "/css/**", "/js/**", "/images/**").permitAll()

                        // All other requests MUST be authenticated.
                        // The actual granular permission checks will now happen inside the Controllers!
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/perform_login")
                        .successHandler((request, response, authentication) -> {

                            String authorities = authentication.getAuthorities().toString();
                            // Get which portal user selected during login
                            String portalType = request.getParameter("portalType");

                            boolean isClient = authentication.getAuthorities()
                                .stream()
                                .map(auth -> auth.getAuthority())
                                .anyMatch(authority ->
                                        authority.equalsIgnoreCase("ROLE_CLIENT")
                                                || authority.equalsIgnoreCase("client_dashboard_view"));

                            // PORTAL VALIDATION
                            // Client trying Employee Portal
                            if ("employee".equalsIgnoreCase(portalType) && isClient) {

                                request.getSession().setAttribute(
                                        "SPRING_SECURITY_LAST_EXCEPTION",
                                        new RuntimeException("Client users cannot login through Employee Portal.")
                                );

                                String referer = request.getHeader("Referer");
                                if (referer != null && !referer.isEmpty()) {
                                    String url = referer.replaceAll("[?&]error=true", "");
                                    url += url.contains("?") ? "&error=true" : "?error=true";
                                    response.sendRedirect(url);
                                } else {
                                    response.sendRedirect("/login?error=true");
                                }
                                return;
                            }

                            // Employee/Admin trying Client Portal
                            if ("client".equalsIgnoreCase(portalType) && !isClient) {

                                request.getSession().setAttribute(
                                        "SPRING_SECURITY_LAST_EXCEPTION",
                                        new RuntimeException("Employee users cannot login through Client Portal.")
                                );
                                
                                String referer = request.getHeader("Referer");
                                if (referer != null && !referer.isEmpty()) {
                                    String url = referer.replaceAll("[?&]error=true", "");
                                    url += url.contains("?") ? "&error=true" : "?error=true";
                                    response.sendRedirect(url);
                                } else {
                                    response.sendRedirect("/login?error=true");
                                }
                                return;
                            }

                            // NORMAL ROLE-BASED ROUTING
                            String customRedirectUrl = request.getParameter("redirectUrl");
                            if (customRedirectUrl != null && !customRedirectUrl.isEmpty()) {
                                response.sendRedirect(customRedirectUrl);
                            }
                            else if (authorities.contains("admin_dashboard_view") || !isClient) {
                                response.sendRedirect("/default-redirect");
                            }
                            else {
                                response.sendRedirect("/client/dashboard");
                            }
                        })
                        .failureHandler((request, response, exception) -> {
                            request.getSession().setAttribute("SPRING_SECURITY_LAST_EXCEPTION", exception);
                            String referer = request.getHeader("Referer");
                            if (referer != null && !referer.isEmpty()) {
                                String url = referer.replaceAll("[?&]error=true", "");
                                url += url.contains("?") ? "&error=true" : "?error=true";
                                response.sendRedirect(url);
                            } else {
                                response.sendRedirect("/login?error=true");
                            }
                        })
                        .permitAll()
                )
                .userDetailsService(customUserDetailsService)
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}
