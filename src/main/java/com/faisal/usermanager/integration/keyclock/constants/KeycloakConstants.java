package com.faisal.usermanager.integration.keyclock.constants;

public final class KeycloakConstants {

    // Routing Constants
    public final static String ADMIN_API = "/admin/realms";
    public final static String REALMS_ENDPOINT = "/realms";
    public final static String CLIENT_TOKEN_ENDPOINT = "/protocol/openid-connect/token";

    // GENERAL Constants
    /**
     * Prefix used for realm level roles.
     */
    public static final String PREFIX_ROLE = "ROLE_";
    /**
     * Name of the claim containing the realm level roles
     */
    public static final String CLAIM_REALM_ACCESS = "realm_access";
    /**
     * Name of the claim containing roles. (Applicable to realm and resource level.)
     */
    public static final String CLAIM_ROLES = "roles";

    public enum RequiredAction {
        VERIFY_EMAIL, UPDATE_PROFILE, CONFIGURE_TOTP, UPDATE_PASSWORD, TERMS_AND_CONDITIONS
    }
}
