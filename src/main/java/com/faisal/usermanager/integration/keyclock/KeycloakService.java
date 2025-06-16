package com.faisal.usermanager.integration.keyclock;

import com.faisal.usermanager.common.exceptions.*;
import com.faisal.usermanager.common.lookups.LookupStringIdResponseDto;
import com.faisal.usermanager.integration.keyclock.constants.KeycloakConstants;
import com.faisal.usermanager.integration.keyclock.payload.KeycloakCreateUserRequest;
import com.faisal.usermanager.integration.keyclock.payload.KeycloakRealmRoleRequest;
import com.faisal.usermanager.integration.keyclock.payload.KeycloakRealmRoleResponse;
import com.faisal.usermanager.integration.keyclock.payload.KeycloakUserResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final KeycloakClient keycloakClient;

    @Value("${user-manager.keycloak.realm}")
    private String realmName;

    @Value("${user-manager.keycloak.client-id}")
    private String clientID;

    @Value("${user-manager.keycloak.user-redirect-url}")
    private String userRedirectUrl;

    /**
     * @param userId    user's id in database.
     * @param email     The unique email of the entity (service account / user) on Keycloak.
     * @param firstName user's first name.
     * @param lastName  user's last name.
     * @param role      The role to be assigned to the user once it has been created.
     * @return The user's id in keycloak
     * @throws ConflictException     is thrown in case the email already exists.
     * @throws FailedEmailException  is thrown is case the service failed to sent verification email.
     * @throws ForbiddenException    is thrown in case the client has no permissions to perform the current operation.
     * @throws UnknownFeignException when service unavailable or failing.
     */
    public String createUser(
            String userId,
            String email,
            String firstName,
            String lastName,
            LookupStringIdResponseDto role
    ) throws ConflictException, ForbiddenException, UnknownFeignException, FailedEmailException {

        List<String> requiredActions = List.of(
                KeycloakConstants.RequiredAction.UPDATE_PASSWORD.name(),
                KeycloakConstants.RequiredAction.VERIFY_EMAIL.name(),
                KeycloakConstants.RequiredAction.CONFIGURE_TOTP.name()
        );

        addKeycloakUserWithIdAttribute(userId, email, firstName, lastName, requiredActions);

        //retrieve created user
        KeycloakUserResponse keycloakUserResponse = getUserByEmail(email);

        //assign role to the user
        try {
            addRoleToUser(
                    keycloakUserResponse.getId(),
                    role
            );
        } catch (ForbiddenException | UnknownFeignException exception) {
            deleteUser(keycloakUserResponse.getId());
            throw exception;
        }

        try {
            //send email to user, to execute the actions above.
            keycloakClient.sendEmailToExecuteParticularActions(
                    realmName,
                    keycloakUserResponse.getId(),
                    requiredActions,
                    userRedirectUrl,
                    clientID
            );
        } catch (Exception e) {
            deleteUser(keycloakUserResponse.getId());

            log.error("Email failed to be sent to: {}", email, e);
            throw new FailedEmailException(ErrorMessage.KEYCLOAK_EMAIL_MESSAGE_FAILED);
        }

        return keycloakUserResponse.getId();
    }

    /**
     * @param userId          user's id in database.
     * @param email           The unique email of the entity (service account / user) on Keycloak.
     * @param firstName       user's first name.
     * @param lastName        user's last name.
     * @param requiredActions list of actions that are required to activate a user.
     * @throws ConflictException     is thrown in case the email already exists.
     * @throws ForbiddenException    is thrown in case the client has no permissions to perform the current operation.
     * @throws UnknownFeignException when service unavailable or failing.
     */
    private void addKeycloakUserWithIdAttribute(
            String userId,
            String email,
            String firstName,
            String lastName,
            List<String> requiredActions
    ) throws ConflictException, ForbiddenException, UnknownFeignException {

        Map<String, String> userAttributes = new HashMap<>() {
            {
                put("user_id", userId);
            }
        };

        KeycloakCreateUserRequest keycloakCreateUserDto = KeycloakCreateUserRequest
                .builder()
                .emailVerified(false)
                .enabled(true)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .attributes(userAttributes)
                .requiredActions(requiredActions)
                .build();

        //create user & assign role
        try {
            keycloakClient.createUser(keycloakCreateUserDto, realmName);
        } catch (FeignException.Conflict conflict) {
            throw new ConflictException(ErrorMessage.CONFLICT_USER_EMAIL_EXISTS);
        } catch (FeignException.Forbidden exception) {
            throw new ForbiddenException(ErrorMessage.KEYCLOAK_FORBIDDEN_INTERNAL);
        } catch (Exception e) {
            throw new UnknownFeignException(ErrorMessage.UNKNOWN_FEIGN_ERROR);
        }
    }

    /**
     * Used to retrieve a user or a client's service account through the email.
     *
     * @param email The unique email of the entity (service account / user) on Keycloak.
     * @return the user's response.
     * @throws UserNotFoundException in case no such user exists.
     * @throws ForbiddenException    is thrown in case the client has no permissions to perform the current operation.
     * @throws UnknownFeignException when service unavailable or failing.
     */
    private KeycloakUserResponse getUserByEmail(String email) throws UserNotFoundException, ForbiddenException, UnknownFeignException {
        List<KeycloakUserResponse> usersWithEmail;

        try {
            usersWithEmail = keycloakClient.getUserByEmail(realmName, email).getBody();
        } catch (FeignException.Forbidden exception) {
            throw new ForbiddenException(ErrorMessage.KEYCLOAK_FORBIDDEN_INTERNAL);
        } catch (Exception e) {
            throw new UnknownFeignException(ErrorMessage.UNKNOWN_FEIGN_ERROR);
        }

        if (usersWithEmail == null || usersWithEmail.isEmpty()) {
            throw new UserNotFoundException(ErrorMessage.USER_NOT_FOUND);
        }

        return usersWithEmail.get(0);
    }

    /**
     * Assign a role to a user who already exists on Keycloak realm
     * <br>Note: this role must already exist on the realm.
     *
     * @param userId The unique id of the user on Keycloak.
     * @param role   The role to be assigned to the user.
     * @throws RoleNotFoundException The exception to be thrown in case the role doesn't exist.
     * @throws ForbiddenException    is thrown in case the client has no permissions to perform the current operation.
     * @throws UnknownFeignException when service unavailable or failing.
     */
    private void addRoleToUser(
            String userId,
            LookupStringIdResponseDto role
    ) throws RoleNotFoundException, ForbiddenException, UnknownFeignException {

        List<KeycloakRealmRoleRequest> keycloakRealmRoleRequestList = List.of(
                KeycloakRealmRoleRequest
                        .builder()
                        .id(role.getId())
                        .name(role.getName())
                        .build()
        );

        try {
            keycloakClient.addRealmRolesToUser(
                    realmName,
                    userId,
                    keycloakRealmRoleRequestList
            );
        } catch (FeignException.NotFound e) {
            throw new RoleNotFoundException(ErrorMessage.USER_ROLE_NOT_FOUND);
        } catch (FeignException.Forbidden e) {
            throw new ForbiddenException(ErrorMessage.KEYCLOAK_FORBIDDEN_INTERNAL);
        } catch (Exception e) {
            throw new UnknownFeignException(ErrorMessage.UNKNOWN_FEIGN_ERROR);
        }
    }

    /**
     * Delete a user from Keycloak.
     *
     * @param userId The unique id of the user on Keycloak.
     * @throws ForbiddenException    is thrown in case the client has no permissions to perform the current operation.
     * @throws UnknownFeignException when service unavailable or failing.
     */
    public void deleteUser(String userId) throws ForbiddenException, UnknownFeignException {

        try {
            keycloakClient.deleteUser(realmName, userId);
        } catch (FeignException.Forbidden exception) {
            throw new ForbiddenException(ErrorMessage.KEYCLOAK_FORBIDDEN_INTERNAL);
        } catch (Exception e) {
            throw new UnknownFeignException(ErrorMessage.UNKNOWN_FEIGN_ERROR);
        }
    }

    public List<KeycloakRealmRoleResponse> getRealmRoles() throws ForbiddenException, UnknownFeignException {
        try {
            ResponseEntity<List<KeycloakRealmRoleResponse>> response = keycloakClient.getRealmRoles(realmName);
            return response.getBody();
        } catch (FeignException.Forbidden exception) {
            throw new ForbiddenException(ErrorMessage.KEYCLOAK_FORBIDDEN_INTERNAL);
        } catch (Exception e) {
            throw new UnknownFeignException(ErrorMessage.UNKNOWN_FEIGN_ERROR);
        }
    }

}
