package com.faisal.usermanager.integration.keyclock;

import com.faisal.usermanager.integration.keyclock.constants.KeycloakConstants;
import com.faisal.usermanager.integration.keyclock.payload.KeycloakCreateUserRequest;
import com.faisal.usermanager.integration.keyclock.payload.KeycloakRealmRoleRequest;
import com.faisal.usermanager.integration.keyclock.payload.KeycloakRealmRoleResponse;
import com.faisal.usermanager.integration.keyclock.payload.KeycloakUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping(
            value = KeycloakConstants.ADMIN_API + "/{realm}/users",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<KeycloakUserResponse>> getUserByEmail(
            @PathVariable String realm,
            @RequestParam(value = "email") String email
    );

    @PostMapping(KeycloakConstants.ADMIN_API + "/{realm}/users/{user_id}/role-mappings/realm")
    ResponseEntity<Void> addRealmRolesToUser(
            @PathVariable String realm,
            @PathVariable("user_id") String userId,
            @RequestBody List<KeycloakRealmRoleRequest> keycloakRealmRoles
    );

    @DeleteMapping(KeycloakConstants.ADMIN_API + "/{realm}/users/{user_id}")
    ResponseEntity<Void> deleteUser(
            @PathVariable String realm,
            @PathVariable("user_id") String userId
    );

    @PutMapping(KeycloakConstants.ADMIN_API + "/{realm}/users/{id}/execute-actions-email")
    ResponseEntity<Void> sendEmailToExecuteParticularActions(
            @PathVariable String realm,
            @PathVariable String id,
            @RequestBody List<String> actions,
            @RequestParam(value = "redirect_uri") String redirectUri,
            @RequestParam(value = "client_id") String clientId
    );

    @GetMapping(
            value = KeycloakConstants.ADMIN_API + "/{realm}/roles",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<KeycloakRealmRoleResponse>> getRealmRoles(@PathVariable String realm);

}
