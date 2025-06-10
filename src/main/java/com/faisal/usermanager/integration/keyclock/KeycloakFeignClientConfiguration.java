package com.faisal.usermanager.integration.keyclock;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakFeignClientConfiguration {
    @Value("${user-manager.keycloak.client-id}")
    private String clientId;

    @Value("${user-manager.keycloak.client-secret}")
    private String clientSecret;

    @Value("${user-manager.keycloak.realm}")
    private String realmName;

    @Value("${user-manager.keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${user-manager.keycloak.audience}")
    private String audience;

    @Value("${user-manager.keycloak.external-services-audience}")
    private String externalServicesAudience;

    @Bean
    public RequestInterceptor keycloakClientInterceptor() {
        return new KeycloakClientRequestInterceptor(new KeycloakAuth(clientId, clientSecret, realmName, authServerUrl, audience));
    }

    @Bean
    public KeycloakAuth keycloakExternalAuth() {
        return new KeycloakAuth(clientId, clientSecret, realmName, authServerUrl, externalServicesAudience);
    }

}
