package com.faisal.usermanager.integration.keyclock.payload;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class KeycloakRealmRoleResponse {
    private String id;
    private String name;
}
