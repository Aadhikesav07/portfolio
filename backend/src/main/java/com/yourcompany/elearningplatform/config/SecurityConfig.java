    package com.yourcompany.elearningplatform.config;

    import com.yourcompany.elearningplatform.security.CustomUserDetailsService;
    import com.yourcompany.elearningplatform.security.JwtAuthenticationFilter;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
    import org.springframework.web.cors.CorsConfiguration;
    import org.springframework.web.cors.CorsConfigurationSource;
    import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

    import java.util.Arrays;

    @Configuration
    public class SecurityConfig {

        @Autowired
        private CustomUserDetailsService customUserDetailsService;

        @Autowired
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf().disable()
                .cors().and()
                .authorizeHttpRequests(auth -> auth
                    // Public endpoints
                    .requestMatchers("/api/users/login", "/api/users/register").permitAll()
                    .requestMatchers("/api/chatbot/**").permitAll() // AI Chatbot - read-only access
                    
                    // Admin only endpoints
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .requestMatchers("/api/courses", "/api/courses/**").hasAnyRole("ADMIN", "INSTRUCTOR", "STUDENT")
                    .requestMatchers("/api/users/**").hasAnyRole("ADMIN", "INSTRUCTOR", "STUDENT")
                    
                    // Exam endpoints - handled in controller for role-based CRUD
                    .requestMatchers("/api/exams/**").hasAnyRole("ADMIN", "INSTRUCTOR", "STUDENT")
                    
                    // Assignment endpoints
                    .requestMatchers("/api/assignments/**").hasAnyRole("ADMIN", "INSTRUCTOR", "STUDENT")
                    
                    // Certificate endpoints
                    .requestMatchers("/api/certificates/**").hasAnyRole("ADMIN", "INSTRUCTOR", "STUDENT")
                    
                    // Student progress
                    .requestMatchers("/api/progress/**").hasAnyRole("ADMIN", "INSTRUCTOR", "STUDENT")
                    
                    .anyRequest().authenticated()
                )   
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

            http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
            configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            configuration.setAllowedHeaders(Arrays.asList("*"));
            configuration.setAllowCredentials(true);
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            return source;
        }

        @Bean
        public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
            return http.getSharedObject(AuthenticationManagerBuilder.class)
                    .userDetailsService(customUserDetailsService)
                    .passwordEncoder(passwordEncoder)
                    .and()
                    .build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
