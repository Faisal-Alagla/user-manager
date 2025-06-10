package com.faisal.usermanager.integration.keyclock;

import com.faisal.usermanager.common.exceptions.ErrorMessage;
import com.faisal.usermanager.common.exceptions.ForbiddenException;
import com.faisal.usermanager.common.exceptions.ServiceConnectException;
import com.faisal.usermanager.integration.keyclock.constants.KeycloakConstants;
import com.faisal.usermanager.integration.keyclock.payload.RetrieveTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;

@Slf4j
public class KeycloakAuth {

    private final String clientId;
    private final String clientSecret;
    private final String realmName;
    private final String authServerUrl;
    private String accessToken;
    private long tokenExpiresAt;
    private final String audience;

    public KeycloakAuth(String clientId, String clientSecret, String realmName, String authServerUrl, String audience) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.realmName = realmName;
        this.accessToken = "";
        this.authServerUrl = authServerUrl;
        this.tokenExpiresAt = 0;
        this.audience = audience;
    }

    /**
     * Retrieve an access token from Keycloak using client credentials auth flow.
     */
    private String generateClientAccessToken() throws ServiceConnectException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        Map<String, String> map = Map.of("grant_type", "client_credentials",
                "client_id", this.clientId,
                "client_secret", this.clientSecret,
                "scope", this.audience);

        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        map.forEach(multiValueMap::add);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(multiValueMap, headers);

        String url = this.authServerUrl + KeycloakConstants.REALMS_ENDPOINT +
                "/" + this.realmName + KeycloakConstants.CLIENT_TOKEN_ENDPOINT;

        ResponseEntity<RetrieveTokenResponse> response = restTemplate
                .exchange(url, HttpMethod.POST, request, RetrieveTokenResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            RetrieveTokenResponse tokenResponse = response.getBody();
            assert tokenResponse != null;
            this.tokenExpiresAt = Instant.now().getEpochSecond() + tokenResponse.getSecondsUntilExpiration();
            log.info("Updating token expiry time to " + this.tokenExpiresAt);
            return tokenResponse.getAccessToken();
        } else if (response.getStatusCode().is4xxClientError()) {
            log.error("keycloak feign get key 400 response in request interceptor");

            throw new ForbiddenException(ErrorMessage.KEYCLOAK_FORBIDDEN_INTERNAL);
        } else {
            throw new ServiceConnectException(ErrorMessage.KEYCLOAK_SERVICE_DOWN);
        }
    }

    public boolean isTokenExpired() {
        long currentTime = Instant.now().getEpochSecond();
        boolean isExpired = currentTime - 5 > tokenExpiresAt;
        return isExpired;
    }

    public String getClientAccessToken() {
        if (isTokenExpired()) {
            try {
                this.accessToken = generateClientAccessToken();
            } catch (Exception e) { // TODO: use ServiceConnectException instead
                log.error("keycloak feign connect exception in request interceptor", e);
                throw new RuntimeException(e);
            }
        }
        return this.accessToken;
    }
}
