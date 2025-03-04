package com.faisal.usermanager.user;

public class UserMapper {

    /**
     * maps a User object to UserResponseDto
     *
     * @param user The User object
     * @return UserResponseDto object containing the passed User data
     */
    public static UserResponseDto mapToUserResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dateOfBirth(user.getDateOfBirth())
                .email(user.getEmail())
                .phone(user.getPhone())
                .roleId(user.getRoleId())
                // TODO: profile image is a placeholder, should be fetched from object store and converted to base64
                .profileImageBase64(user.getProfileImageUrl())
                .build();
    }

    /**
     * maps a UserCreationDto object to User
     *
     * @param userCreationDto The UserCreationDto object
     * @return User object containing the passed UserCreationDto data
     */
    public static User mapToUser(UserCreationDto userCreationDto) {
        return User.builder()
                .firstName(userCreationDto.getFirstName())
                .lastName(userCreationDto.getLastName())
                .dateOfBirth(userCreationDto.getDateOfBirth())
                .email(userCreationDto.getEmail())
                .phone(userCreationDto.getPhone())
                .roleId(userCreationDto.getRoleId())
                .build();
    }

}
