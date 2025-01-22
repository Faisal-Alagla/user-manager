package com.faisal.usermanager.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Schema(
        name = "UserCreationRequest",
        description = "Schema to hold User information"
)
public class UserCreationDto {

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    //TODO: add email exists validation
    @Email(message = "email format is invalid")
    private String email;

    //TODO: add phone format validation
    //TODO: add phone exists validation
    private String phone;

    //TODO: add role validation
    private Integer roleId;

    //TODO: add file type validation
    private MultipartFile profileImage;

    //TODO: add group exists validation (nullable = true)
    private UUID groupId;

}
