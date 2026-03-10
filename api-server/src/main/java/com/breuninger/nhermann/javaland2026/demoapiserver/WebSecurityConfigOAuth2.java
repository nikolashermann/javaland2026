package com.breuninger.nhermann.javaland2026.demoapiserver;

import static org.springframework.security.config.http.SessionCreationPolicy.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtBearerTokenAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
@EnableWebSecurity
@Profile("oauth2")
public class WebSecurityConfigOAuth2 {

  @Bean
  public SecurityFilterChain oauth2(HttpSecurity http) {
    http.securityMatcher(PathPatternRequestMatcher.withDefaults().matcher("/api/**"))
        .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
        .oauth2ResourceServer(
            oauth2 ->
                oauth2.jwt(
                    jwt ->
                        jwt.jwtAuthenticationConverter(
                            new JwtBearerTokenAuthenticationConverter())))
        .authorizeHttpRequests(
            registry -> registry.anyRequest().hasAuthority("SCOPE_api-server-scope"));

    return http.build();
  }
}
