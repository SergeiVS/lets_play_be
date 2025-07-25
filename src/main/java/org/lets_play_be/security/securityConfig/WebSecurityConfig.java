package org.lets_play_be.security.securityConfig;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.repository.AppUserRepository;
import org.lets_play_be.security.filter.JwtAuthenticationFilter;
import org.lets_play_be.security.handler.AppAccessDeniedHandler;
import org.lets_play_be.security.handler.AppEntryPointHandler;
import org.lets_play_be.security.utils.AppUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, proxyTargetClass = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final AppUserRepository userRepository;
    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new AppUserDetailsService(userRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                new AntPathRequestMatcher("/api/v1/user/**"),
                                new AntPathRequestMatcher("/api/v1/sse"),
                                new AntPathRequestMatcher("/api/v1/invite"),
                                new AntPathRequestMatcher("/api/v1/auth/logout"),
                                new AntPathRequestMatcher("/api/v1/lobby/**", "POST"),
                                new AntPathRequestMatcher("/api/v1/lobby/**", "PATCH"),
                                new AntPathRequestMatcher("/api/v1/lobby/**", "DELETE")
                        ).hasAnyRole("ADMIN", "USER")
                        .requestMatchers(
                                new AntPathRequestMatcher("/swagger-ui/**"),
                                new AntPathRequestMatcher("/v3/api-docs/**"),
                                new AntPathRequestMatcher("/v3/api-docs.yaml"),
                                new AntPathRequestMatcher("/swagger-ui.html"),
                                new AntPathRequestMatcher("/api/v1/auth/login"),
                                new AntPathRequestMatcher("/api/v1/auth/refresh"),
                                new AntPathRequestMatcher("/api/v1/auth/register")
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .cors(cors -> corsConfigurationSource())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new AppEntryPointHandler())
                        .accessDeniedHandler(new AppAccessDeniedHandler())
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.applyPermitDefaultValues();
        config.setAllowedOrigins(List.of("capacitor://localhost", "ionic://localhost", "http://localhost:8080", "http" +
                "://localhost:4200", "http://localhost:9000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("Content-Type", "Authorization"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
