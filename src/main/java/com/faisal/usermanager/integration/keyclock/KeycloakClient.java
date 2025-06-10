package com.faisal.usermanager.integration.keyclock;

import com.faisal.usermanager.integration.keyclock.constants.KeycloakConstants;
import com.faisal.usermanager.integration.keyclock.payload.KeycloakCreateUserRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        value = "keycloakClient",
        url = "${user-manager.keycloak.auth-server-url}",
        configuration = KeycloakFeignClientConfiguration.class
)
public interface KeycloakClient {

    @PostMapping(KeycloakConstants.ADMIN_API + "/{realm}/users")
    ResponseEntity<Void> createUser(
            @RequestBody KeycloakCreateUserRequest createUserDto,
            @PathVariable String realm
    );

}
