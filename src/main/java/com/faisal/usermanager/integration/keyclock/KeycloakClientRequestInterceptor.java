package com.faisal.usermanager.integration.keyclock;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeycloakClientRequestInterceptor implements RequestInterceptor {
    private final KeycloakAuth auth;

    public KeycloakClientRequestInterceptor(KeycloakAuth auth) {
        this.auth = auth;
    }

    @Override
    public void apply(RequestTemplate template) {
        String authHeaderName = "Authorization";
        template.header(authHeaderName, "Bearer " + this.auth.getClientAccessToken());
    }
}
