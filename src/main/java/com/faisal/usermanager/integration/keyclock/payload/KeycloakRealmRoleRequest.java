package com.faisal.usermanager.integration.keyclock.payload;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class KeycloakRealmRoleRequest {
    private String id;
    private String name;
}
