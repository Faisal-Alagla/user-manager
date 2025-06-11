package com.faisal.usermanager.integration.keyclock.payload;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class KeycloakUserResponse {
    boolean emailVerified;
    boolean enabled;
    private String id;
    private String email;
    private String firstName;
    private String lastName;
}
