package com.faisal.usermanager.integration.keyclock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final KeycloakClient keycloakClient;

    private final KeycloakAuth keycloakAuth;

    @Value("${user-manager.keycloak.realm}")
    private String realmName;

    @Value("${user-manager.keycloak.client-id}")
    private String clientID;

    @Value("${user-manager.keycloak.user-redirect-url}")
    private String userRedirectUrl;

}
