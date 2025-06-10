package com.faisal.usermanager.integration.keyclock.payload;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KeycloakCredentialRequest {
    private String type;
    private String value;
    private boolean temporary;
}
