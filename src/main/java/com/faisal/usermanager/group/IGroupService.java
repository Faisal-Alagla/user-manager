package com.faisal.usermanager.group;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IGroupService {

    /**
     * Creates a new group
     *
     * @param groupCreationDto GroupCreationDto object containing the group data to be created
     * @return GroupResponseDto containing the created group data
     */
    GroupResponseDto createGroup(GroupCreationDto groupCreationDto);

    /**
     * Invites a user to a group
     *
     * @param groupId id of the target group
     * @param userId id of the user to be invited
     * @return GroupResponse containing the group data
     */
    GroupResponseDto inviteUser(UUID groupId, UUID userId);

    /**
     * Get a group by id
     *
     * @param groupId id of the group to be fetched
     * @return GroupResponseDto object containing the fetched group data
     */
    GroupResponseDto getGroup(UUID groupId);

    /**
     * Get all active groups (paginated)
     *
     * @return a Page<GroupResponseDto> containing the groups data
     */
    Page<GroupResponseDto> getAllGroups(Pageable pageable);

    /**
     * Update a group by id
     *
     * @param groupUpdateDto GroupUpdateDto object containing the new group data
     * @param groupId  id of the group to be updated
     * @return GroupResponseDto object containing the updated group data
     */
    GroupResponseDto updateGroup(GroupUpdateDto groupUpdateDto, UUID groupId);

    /**
     * Soft delete a group by id
     *
     * @param groupId id of the Group to be deleted
     */
    void deleteGroup(UUID groupId);

    /**
     * Checks whether a group exists and active
     *
     * @param groupId id of the group to be checked
     * @return true if the group exists and active, false otherwise
     */
    boolean groupExists(UUID groupId);

}
