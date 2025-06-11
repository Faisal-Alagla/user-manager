package com.faisal.usermanager.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@Schema(
        name = "UserResponse",
        description = "Schema to hold User information as a response"
)
public class UserResponseDto {

    private UUID id;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private String email;

    private String phone;

    private String roleId;

    private String profileImageBase64;

}
