package com.faisal.usermanager.common.security;

import com.faisal.usermanager.integration.keyclock.constants.KeycloakConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.UUID;

@Slf4j
public class SecurityUtils {

    private static Authentication getAuthentication() {
        try {
            return SecurityContextHolder.getContext().getAuthentication();
        } catch (Exception e) {
            log.error("Trying to access security context while no authenticated principal exists");
            throw e;
        }
    }

    private static Jwt getAuthenticationJwt() {
        try {
            return (Jwt) getAuthentication().getPrincipal();
        } catch (Exception e) {
            log.error("Error getting authentication principle", e);
            throw e;
        }
    }

    /**
     * Returns the current authenticated user email
     *
     * @return user's email
     */
    public static String getCurrentUserEmail() {

        Jwt jwt = getAuthenticationJwt();

        assert jwt.hasClaim("email");
        return jwt.getClaimAsString("email");
    }

    /**
     * Returns the current authenticated user id
     *
     * @return user's id
     */
    public static UUID getCurrentUserId() {

        Jwt jwt = getAuthenticationJwt();

        assert jwt.hasClaim("user_id");
        String userId = jwt.getClaimAsString("user_id");

        return UUID.fromString(userId);
    }

    /**
     * Returns the currently authenticated user's roles.
     *
     * @return List of current user roles.
     */
    private static List<String> getCurrentUserRoles() {

        try {
            List<SimpleGrantedAuthority> authorities = (List<SimpleGrantedAuthority>) getAuthentication().getAuthorities();
            return authorities
                    .stream()
                    .map(simpleGrantedAuthority ->
                            simpleGrantedAuthority.getAuthority()
                                    .replaceFirst("^" + KeycloakConstants.PREFIX_ROLE, "")
                    )
                    .toList();
        } catch (Exception e) {
            log.error("Error getting authorities", e);
            throw e;
        }
    }

    public static String getCurrentUserRole() {

        List<String> acceptedUserRoleList = List.of(
                SecurityConstants.ADMIN_ROLE,
                SecurityConstants.USER_ROLE
        );

        return getCurrentUserRoles().stream()
                .filter(acceptedUserRoleList::contains)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("user role not found"));
    }
}
