package com.larasierra.movietickets.movie.config;

import com.larasierra.movietickets.shared.util.AuthInfo;
import org.springframework.aop.Advisor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = false)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            // define public endpoints, role-based access check is done through Method Security in the service layer
            .authorizeHttpRequests(authorize -> authorize
                // event details
                .requestMatchers(HttpMethod.GET, "/event/{id}").permitAll()
                // event options
                .requestMatchers(HttpMethod.GET, "/event-option/", "/event-option/{id}").permitAll()
                // event age restriction
                .requestMatchers(HttpMethod.GET, "/age-restriction", "/age-restriction/{id}").permitAll()
                // event type
                .requestMatchers(HttpMethod.GET, "/type", "/type/{id}").permitAll()
                // event venue
                .requestMatchers(HttpMethod.GET, "/venue", "/venue/{id}").permitAll()
                // event FAQ
                .requestMatchers(HttpMethod.GET, "/faq", "/faq/{id}").permitAll()
                // organizer details
                .requestMatchers(HttpMethod.GET, "/organizer/{id}").permitAll()
                // open api
                .requestMatchers("/v3/api-docs/movie-service*/**", "/swagger-ui*/**").permitAll()
                // any other request must be authenticated
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer().jwt(jwt -> jwt
                .jwtAuthenticationConverter(authenticationConverter())
            );
        return http.build();
    }

    private JwtAuthenticationConverter authenticationConverter() {
        var authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("cognito:groups");
        authoritiesConverter.setAuthorityPrefix("ROLE_");

        var authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        return authenticationConverter;
    }

    /**
     * Along with @{@link EnableMethodSecurity} with prePostEnabled = false, enables only the @PreAuthorize annotation
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    Advisor preAuthorize() {
        return AuthorizationManagerBeforeMethodInterceptor.preAuthorize();
    }

    @Bean
    public AuthInfo authInfo(JwtDecoder jwtDecoder) {
        return new AuthInfo(jwtDecoder);
    }

}
