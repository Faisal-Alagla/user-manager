package com.faisal.usermanager.group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Data
@Schema(
        name = "GroupResponse",
        description = "Schema to hold Group information as a response"
)
public class GroupResponseDto {

    private UUID ID;

    private String name;

    private String description;

    private MultipartFile groupImage;

    private UUID ownerId;

    private List<UUID> memberIds;

    private Integer visibilityId;

}
