package com.faisal.usermanager.integration.keyclock;

import com.faisal.usermanager.integration.keyclock.constants.KeycloakConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        // Collection that will hold the extracted roles
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        // Get the part of the access token that holds the roles assigned on realm level
        Map<String, Collection<String>> realmAccess = jwt.getClaim(KeycloakConstants.CLAIM_REALM_ACCESS);

        // Verify that the claim exists and is not empty
        if (realmAccess != null && !realmAccess.isEmpty()) {
            // From the realm_access claim get the roles
            Collection<String> roles = realmAccess.get(KeycloakConstants.CLAIM_ROLES);
            // Check if any roles are present
            if (roles != null && !roles.isEmpty()) {
                // Iterate of the roles and add them to the granted authorities
                Collection<GrantedAuthority> realmRoles = roles.stream()
                        // Prefix all realm roles with `PREFIX_ROLE`
                        .map(role -> new SimpleGrantedAuthority(KeycloakConstants.PREFIX_ROLE + role))
                        .collect(Collectors.toList());
                grantedAuthorities.addAll(realmRoles);
            }
        }
        return grantedAuthorities;
    }
}
