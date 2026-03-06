package com.breuninger.nhermann.javaland2026.demoapiclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestClient;

@Configuration
@Profile("basic")
public class RestClientConfigBasicAuth {

  @Value("${basic.username}")
  private String username;

  @Value("${basic.password}")
  private String password;

  @Bean
  public RestClient basicAuthRestClient(RestClient.Builder builder) {
    return builder
        .defaultHeader(
            "Authorization",
            "Basic "
                + java.util.Base64.getEncoder()
                    .encodeToString((username + ":" + password).getBytes()))
        .build();
  }
}
