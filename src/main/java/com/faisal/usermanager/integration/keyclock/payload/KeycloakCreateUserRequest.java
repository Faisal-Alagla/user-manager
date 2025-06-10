package com.faisal.usermanager.integration.keyclock.payload;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KeycloakCreateUserRequest {
    private String email;
    private String lastName;
    private String firstName;
    private boolean enabled;
    private boolean emailVerified;
    private List<KeycloakCredentialRequest> credentials;
    private List<String> requiredActions;
    private Map<String, String> attributes;
}
