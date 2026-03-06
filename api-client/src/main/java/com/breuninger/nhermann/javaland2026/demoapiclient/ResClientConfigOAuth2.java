package com.breuninger.nhermann.javaland2026.demoapiclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestClient;

@Configuration
@Profile("oauth2")
public class ResClientConfigOAuth2 {

  @Value("${oauth2.registration-id}")
  private String clientRegistrationId;

  @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
  private String clientId;

  @Bean
  public RestClient oauth2RestClient(
      RestClient.Builder builder, OAuth2AuthorizedClientManager authorizedClientManager) {
    return builder.requestInterceptor(getBearerAuthInterceptor(authorizedClientManager)).build();
  }

  private ClientHttpRequestInterceptor getBearerAuthInterceptor(
      OAuth2AuthorizedClientManager authorizedClientManager) {
    return (request, body, execution) -> {
      var authorizeRequest =
          OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistrationId)
              .principal(clientId)
              .build();

      var authorizedClient = authorizedClientManager.authorize(authorizeRequest);
      if (authorizedClient == null || authorizedClient.getAccessToken() == null) {
        throw new RuntimeException("Could not obtain access token");
      }

      var accessTokenValue = authorizedClient.getAccessToken().getTokenValue();

      System.out.println("accessToken = " + accessTokenValue);

      request.getHeaders().setBearerAuth(accessTokenValue);
      return execution.execute(request, body);
    };
  }

  @Bean
  public OAuth2AuthorizedClientManager authorizedClientManager(
      ClientRegistrationRepository clientRegistrationRepository,
      OAuth2AuthorizedClientRepository authorizedClientRepository) {
    var provider = OAuth2AuthorizedClientProviderBuilder.builder().clientCredentials().build();

    var manager =
        new DefaultOAuth2AuthorizedClientManager(
            clientRegistrationRepository, authorizedClientRepository);

    manager.setAuthorizedClientProvider(provider);
    return manager;
  }
}
