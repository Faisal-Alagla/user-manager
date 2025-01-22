package com.faisal.usermanager.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Schema(
        name = "UserResponse",
        description = "Schema to hold User information as a response"
)
public class UserResponseDto {

    private UUID ID;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private String email;

    private String phone;

    private Integer roleId;

    private MultipartFile profileImage;

    private UUID groupId;

}
