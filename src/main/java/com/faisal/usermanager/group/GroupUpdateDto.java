package com.faisal.usermanager.group;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@Schema(
        name = "GroupUpdateRequest",
        description = "Schema to hold Group information"
)
public class GroupUpdateDto {

    @NotBlank(message = "group name can't be empty")
    @Size(max = 100, message = "name length can't be greater than {max} characters")
    private String name;

    private String description;

    //TODO: add file type validation
    private MultipartFile groupImage;

    //TODO: add user id validation
    private UUID ownerId;

    //TODO: add lookup validation
    private Integer visibilityId;

}
