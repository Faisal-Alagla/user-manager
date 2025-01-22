package com.faisal.usermanager.group;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IGroupService {

    /**
     * Creates a new Group and store it in the DB
     *
     * @param groupCreationDto GroupCreationDto object containing the Group data to be created
     * @return GroupResponseDto containing the created Group data in the DB
     */
    GroupResponseDto createGroup(GroupCreationDto groupCreationDto);

    /**
     * Get the Group from the DB with the matching Group id
     *
     * @param groupId id of the group to be fetched
     * @return GroupResponseDto object containing the fetched group data
     */
    GroupResponseDto getGroup(UUID groupId);

    /**
     * Gets all Groups from the DB
     *
     * @return a List<GroupResponseDto> containing the groups data
     */
    Page<GroupResponseDto> getAllGroups(Pageable pageable);

    /**
     * Updates an existing Group in the DB with the matching Group id
     *
     * @param groupUpdateDto GroupUpdateDto object containing the new updated Group data
     * @param groupId  id of the group to be updated
     * @return GroupResponseDto object containing the updated Group data
     */
    GroupResponseDto updateGroup(GroupUpdateDto groupUpdateDto, UUID groupId);

    /**
     * Soft deletes a Group in the DB with the matching Group id
     *
     * @param groupId id of the Group to be deleted
     */
    void deleteGroup(UUID groupId);

    /**
     * Checks whether a Group exists and active
     *
     * @param groupId id of the Group to be deleted
     * @return true if the group exists and active, false otherwise
     */
    boolean groupExists(UUID groupId);

}
