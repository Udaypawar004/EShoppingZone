package com.apigateway.config;

import com.apigateway.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf().disable()
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/userservice/auth/register", "/userservice/auth/login", "/userservice/auth/forgetPassword").permitAll()
                        .pathMatchers("/userservice/user/getAllUsers").hasRole("ADMIN")
                        .pathMatchers("/userservice/user/**", "/userservice/auth/updatePassword").hasAnyRole("ADMIN", "MERCHANT", "CUSTOMER")
                        .pathMatchers("/productservice/products/getAllProducts").hasAnyRole("CUSTOMER", "ADMIN", "MERCHANT")
                        .pathMatchers("/productservice/products/**", "/notificationservice/notification/**").hasAnyRole("ADMIN", "MERCHANT")
                        .pathMatchers("/cartservice/carts/**").hasAnyRole("MERCHANT", "ADMIN", "CUSTOMER")
                        .pathMatchers("/orderservice/orders/placeOrder", "/orderservice/orders/customer/**", "/orderservice/orders/cancelOrder/**").hasAnyRole("CUSTOMER", "ADMIN")
                        .pathMatchers("/orderservice/orders/getAllOrders", "/orderservice/orders/changeOrderStatus/**").hasAnyRole("MERCHANT", "ADMIN")
                        .pathMatchers("/orderservice/**").hasRole("ADMIN")
                        .pathMatchers("/cartservice/**").hasAnyRole("CUSTOMER", "ADMIN")
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
