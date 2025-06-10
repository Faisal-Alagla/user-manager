package com.faisal.usermanager.common.security;

public class SecurityConstants {
    public static final String[] SWAGGER_URLS = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    /***
     * Roles defined in <a href="https://www.keycloak.org/documentation">KeyCloak</a> realm's setting.
     */
    public static final String USER_ROLES_PREFIX = "TM_USER_";
    //users
    public static final String ADMIN_ROLE = USER_ROLES_PREFIX + "ADMIN";
    public static final String USER_ROLE = USER_ROLES_PREFIX + "USER";

    /***
     * Auths for {@link org.springframework.security.access.prepost.PreAuthorize PreAuthorize}
     */
    public static final String AUTH_ADMIN = "hasAnyRole(" +
            "'" + ADMIN_ROLE + "' " +
            ")";

    public static final String AUTH_USER = "hasAnyRole(" +
            "'" + USER_ROLE + "' " +
            ")";

    public static final String AUTH_ADMIN_OR_USER = "hasAnyRole(" +
            "'" + ADMIN_ROLE + "', " +
            "'" + USER_ROLE + "' " +
            ") ";
}