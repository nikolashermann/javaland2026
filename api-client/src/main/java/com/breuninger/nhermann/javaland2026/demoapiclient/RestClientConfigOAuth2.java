package com.breuninger.nhermann.javaland2026.demoapiclient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.endpoint.RestClientClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

@Configuration
@Profile("oauth2")
public class RestClientConfigOAuth2 {

  @Value("${oauth2.registration-id}")
  private String clientRegistrationId;

  @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
  private String clientId;

  @Bean
  public RestClient oauth2RestClient(
      RestClient.Builder builder, OAuth2AuthorizedClientManager authorizedClientManager) {
    // add request interceptor for oauth2
    return builder.requestInterceptor(getBearerAuthInterceptor(authorizedClientManager)).build();
  }

  private ClientHttpRequestInterceptor getBearerAuthInterceptor(
      OAuth2AuthorizedClientManager authorizedClientManager) {
    return (request, body, execution) -> {
      var authorizeRequest =
          OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistrationId)
              .principal(clientId)
              .build();

      // get (cached) authorized client
      var authorizedClient = authorizedClientManager.authorize(authorizeRequest);
      if (authorizedClient == null || authorizedClient.getAccessToken() == null) {
        throw new RuntimeException("Could not obtain access token");
      }

      // get access token value from authorized client
      var accessTokenValue = authorizedClient.getAccessToken().getTokenValue();

      System.out.println("accessToken = " + accessTokenValue);

      // add authorization header
      request.getHeaders().setBearerAuth(accessTokenValue);
      return execution.execute(request, body);
    };
  }

  @Bean
  public OAuth2AuthorizedClientManager authorizedClientManager(
      ClientRegistrationRepository clientRegistrationRepository,
      OAuth2AuthorizedClientRepository authorizedClientRepository) {

    var provider =
        OAuth2AuthorizedClientProviderBuilder.builder()
            .clientCredentials(
                configurer ->
                    // customize access token response client
                    configurer.accessTokenResponseClient(createAccessTokenResponseClient()))
            .build();

    var manager =
        new DefaultOAuth2AuthorizedClientManager(
            clientRegistrationRepository, authorizedClientRepository);
    manager.setAuthorizedClientProvider(provider);

    return manager;
  }

  private RestClientClientCredentialsTokenResponseClient createAccessTokenResponseClient() {
    var accessTokenResponseClient = new RestClientClientCredentialsTokenResponseClient();
    accessTokenResponseClient.setParametersConverter(
        grantRequest -> {
          var parameters = new LinkedMultiValueMap<String, String>();
          if (grantRequest
              .getClientRegistration()
              .getRegistrationId()
              .equals(clientRegistrationId)) {
            // overwrite client secret
            parameters.set(OAuth2ParameterNames.CLIENT_SECRET, loadSecret());
          }
          return parameters;
        });
    return accessTokenResponseClient;
  }

  public String loadSecret() {
    try {
      return Files.readString(Path.of("/run/secrets/oauth2_client_secret")).trim();
    } catch (IOException e) {
      throw new IllegalStateException("Could not load secret file", e);
    }
  }
}
