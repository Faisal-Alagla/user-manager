package com.faisal.usermanager.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IUserService {

    /**
     * Creates a new User and store it in the DB
     *
     * @param userCreationDto UserCreationDto object containing the User data to be created
     * @return GroupResponseDto containing the created User data in the DB
     */
    UserResponseDto createUser(UserCreationDto userCreationDto);

    /**
     * Get the User from the DB with the matching User id
     *
     * @param userId id of the user to be fetched
     * @return UserResponseDto object containing the fetched user data
     */
    UserResponseDto getUser(UUID userId);

    /**
     * Gets all Users from the DB
     *
     * @return a List<UserResponseDto> containing the users data
     */
    Page<UserResponseDto> getAllUsers(Pageable pageable);

    /**
     * Updates an existing User in the DB with the matching User id
     *
     * @param userUpdateDto UserUpdateDto object containing the new updated user data
     * @param userId  id of the user to be updated
     * @return GroupResponseDto object containing the updated User data
     */
    UserResponseDto updateUser(UserUpdateDto userUpdateDto, UUID userId);

    /**
     * Soft deletes a User in the DB with the matching User id
     *
     * @param userId id of the User to be deleted
     */
    void deleteGroup(UUID userId);

    /**
     * Checks whether a User exists and active
     *
     * @param userId id of the Group to be deleted
     * @return true if the user exists and active, false otherwise
     */
    boolean userExists(UUID userId);

}
