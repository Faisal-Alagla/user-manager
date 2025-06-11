package com.faisal.usermanager.user;

import com.faisal.usermanager.utils.validations.ImageTypeValidation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(
        name = "UserUpdateRequest",
        description = "Schema to hold User information"
)
public class UserUpdateDto {

    //TODO: add email exists (but not owner of it) validation
    @Email(message = "email format is invalid")
    private String email;

    //TODO: add phone format validation
    //TODO: add phone exists (but not owner of it) validation
    private String phone;

    //TODO: add role validation
    private String roleId;

    @ImageTypeValidation
    private MultipartFile profileImage;

}
