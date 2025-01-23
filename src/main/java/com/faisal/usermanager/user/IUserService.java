package com.faisal.usermanager.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IUserService {

    /**
     * Creates a new user
     *
     * @param userCreationDto UserCreationDto object containing the user data to be created
     * @return GroupResponseDto containing the created user data
     */
    UserResponseDto createUser(UserCreationDto userCreationDto);

    /**
     * Get a user by id
     *
     * @param userId id of the user to be fetched
     * @return UserResponseDto object containing the fetched user data
     */
    UserResponseDto getUser(UUID userId);

    /**
     * Get all active users (paginated)
     *
     * @return a Page<UserResponseDto> containing the users data
     */
    Page<UserResponseDto> getAllUsers(Pageable pageable);

    /**
     * Update a user by id
     *
     * @param userUpdateDto UserUpdateDto object containing the new user data
     * @param userId  id of the user to be updated
     * @return GroupResponseDto object containing the updated user data
     */
    UserResponseDto updateUser(UserUpdateDto userUpdateDto, UUID userId);

    /**
     * Soft delete a user by id
     *
     * @param userId id of the user to be deleted
     */
    void deleteUser(UUID userId);

    /**
     * Checks whether a user exists and active
     *
     * @param userId id of the user to be deleted
     * @return true if the user exists and active, false otherwise
     */
    boolean userExists(UUID userId);

}
