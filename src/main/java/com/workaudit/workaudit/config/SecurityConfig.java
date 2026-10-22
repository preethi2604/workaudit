package com.workaudit.workaudit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomUserDetailsService customUserDetailsService) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(customUserDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/actuator/health"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/users"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/users/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/users",
                                "/api/users/**"
                        ).hasAnyRole("ADMIN", "MANAGER")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/users/**"
                        ).hasAnyRole("ADMIN", "MANAGER")

                        .requestMatchers("/api/roles/**")
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/projects"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/projects",
                                "/api/projects/**"
                        ).hasAnyRole("ADMIN", "MANAGER")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/projects/**"
                        ).hasAnyRole("ADMIN", "MANAGER")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/projects/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/domains"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/domains",
                                "/api/domains/**"
                        ).hasAnyRole("ADMIN", "MANAGER")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/domains/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/domains/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/managers"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/managers",
                                "/api/managers/**"
                        ).hasAnyRole("ADMIN", "MANAGER")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/managers/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/managers/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/employees"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/employees",
                                "/api/employees/**"
                        ).hasAnyRole("ADMIN", "MANAGER")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/employees/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/employees/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/reports",
                                "/api/reports/date/**",
                                "/api/reports/date-range"
                        ).hasAnyRole("ADMIN", "MANAGER")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/reports/my"
                        ).hasAnyRole("ADMIN", "MANAGER", "EMPLOYEE")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/reports/*/pdf",
                                "/api/reports/*"
                        ).hasAnyRole("ADMIN", "MANAGER", "EMPLOYEE")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/reports"
                        ).hasAnyRole("ADMIN", "MANAGER", "EMPLOYEE")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/reports/**"
                        ).hasAnyRole("ADMIN", "MANAGER", "EMPLOYEE")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/reports/**"
                        ).hasAnyRole("ADMIN", "MANAGER")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/reports/**"
                        ).hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                .authenticationProvider(authenticationProvider())

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}