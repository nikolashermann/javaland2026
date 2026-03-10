package com.breuninger.nhermann.javaland2026.demoapiserver;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
@EnableWebSecurity
@Profile("basic")
public class WebSecurityConfigBasicAuth {

  @Bean
  public SecurityFilterChain basicAuth(HttpSecurity http) {
    http.securityMatcher(PathPatternRequestMatcher.withDefaults().matcher("/api/**"))
        .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
        .authorizeHttpRequests(registry -> registry.anyRequest().hasRole("API"))
        .httpBasic(Customizer.withDefaults());

    return http.build();
  }

  @Bean
  public UserDetailsManager users(
      PasswordEncoder passwordEncoder,
      @Value("${basic.username}") String username,
      @Value("${basic.password}") String password) {

    UserDetails apiUser =
        User.withUsername(username).password(passwordEncoder.encode(password)).roles("API").build();

    return new InMemoryUserDetailsManager(apiUser);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
