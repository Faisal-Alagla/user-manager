package com.faisal.usermanager.common.lookups;

import com.faisal.usermanager.common.lookups.entities.GroupVisibilityLk;
import com.faisal.usermanager.common.lookups.entities.UserGroupRoleLk;
import com.faisal.usermanager.common.lookups.entities.UserRoleLk;
import com.faisal.usermanager.integration.keyclock.KeycloakService;
import com.faisal.usermanager.integration.keyclock.payload.KeycloakRealmRoleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class LookupService {

    private final LookupRepository lookupRepository;
    private final KeycloakService keycloakService;

    private List<GroupVisibilityLk> groupVisibilityLookupList = List.of();
    private List<UserGroupRoleLk> userGroupRoleLookupList = List.of();
    private List<UserRoleLk> userRoleLookupList = List.of();

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @EventListener(ApplicationReadyEvent.class)
    protected void initializeLookups() {
        log.info("filling lookup values");

        List<KeycloakRealmRoleResponse> keycloakRoles = keycloakService.getRealmRoles();

        lookupRepository.saveUserRoleLookup(
                keycloakRoles.stream().filter(item -> item.getName().toLowerCase().contains("tm_"))
                        .map(item -> {
                            UserRoleLk type = new UserRoleLk();
                            type.setId(item.getId());
                            type.setName(item.getName());
                            return type;
                        })
                        .toList()
        );

        groupVisibilityLookupList = lookupRepository.getGroupVisibilityLookup();
        userGroupRoleLookupList = lookupRepository.getUserGroupRoleLookup();
        userRoleLookupList = lookupRepository.getUserRoleLookup();
    }

    Stream<LookupResponseDto> getLookup(LookupType type) {

        switch (type) {
            case GROUP_VISIBILITY -> {
                return groupVisibilityLookupList.stream().map(LookupResponseDto::fromEntity);
            }
            case USER_GROUP_ROLE -> {
                return userGroupRoleLookupList.stream().map(LookupResponseDto::fromEntity);
            }
            default -> {
                log.error("no values for the given lookup type");
                return null;
            }
        }

    }

    Stream<LookupStringIdResponseDto> getUserRoles() {
        return userRoleLookupList.stream().map(LookupStringIdResponseDto::fromEntity);
    }

    public Optional<LookupResponseDto> findLookupById(LookupType type, Integer id) {
        return getLookup(type)
                .filter(item -> item.getId().equals(id))
                .findFirst();
    }

    public Optional<LookupResponseDto> findLookupByName(LookupType type, String name) {
        return getLookup(type)
                .filter(item -> item.getName().equals(name))
                .findFirst();
    }

    public List<LookupResponseDto> findLookupByNames(LookupType type, List<String> names) {
        return getLookup(type)
                .filter(item -> names.contains(item.getName()))
                .toList();
    }

    public Optional<LookupStringIdResponseDto> findRoleLookupById(String id) {
        return userRoleLookupList.stream().map(LookupStringIdResponseDto::fromEntity)
                .filter(item -> item.getId().equals(id))
                .findFirst();
    }

    public Optional<LookupStringIdResponseDto> findRoleLookupByName(String name) {
        return userRoleLookupList.stream().map(LookupStringIdResponseDto::fromEntity)
                .filter(item -> item.getName().equals(name))
                .findFirst();
    }

}
