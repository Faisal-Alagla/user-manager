package com.faisal.usermanager.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "first name can't be empty")
    @Size(max = 30, message = "first name length can't be greater than {max} characters")
    private String firstName;

    @NotBlank(message = "last name can't be empty")
    @Size(max = 30, message = "last name length can't be greater than {max} characters")
    private String lastName;

    private LocalDate dateOfBirth;

    @NotBlank(message = "email can't be empty")
    //TODO: add email exists validation
    @Email(message = "email format is invalid")
    private String email;

    //TODO: add phone format validation
    //TODO: add phone exists validation (nullable = true)
    private String phone;

    //TODO: add role validation
    private Integer roleId;

    //TODO: add file type validation
    private MultipartFile profileImage;

    //TODO: add group exists validation (nullable = true)
    private UUID groupId;

}
