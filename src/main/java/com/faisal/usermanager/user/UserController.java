package com.faisal.usermanager.user;

import com.faisal.usermanager.utils.constants.BaseRoutingConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(
        name = "User Operations",
        description = "Endpoints for User operations"
)
@RestController
@RequestMapping(path = BaseRoutingConstants.API + BaseRoutingConstants.V1 + BaseRoutingConstants.USER)
@RequiredArgsConstructor
@Validated
public class UserController {

    private final IUserService iUserService;

    @Operation(
            summary = "Create User",
            description = "Add a new user"
    )
    @PostMapping()
    public ResponseEntity<UserResponseDto> createUser(@Valid @ModelAttribute UserCreationDto userCreationDto) {
        UserResponseDto userResponseDto = iUserService.createUser(userCreationDto);

        return new ResponseEntity<>(userResponseDto, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get User",
            description = "Get a specific user by user id"
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable("id") UUID id) {
        UserResponseDto userResponseDto = iUserService.getUser(id);

        return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Update User",
            description = "Update a specific user by user id"
    )
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateIssue(
            @PathVariable("id") UUID id,
            @Valid @ModelAttribute UserUpdateDto userUpdateDto
    ) {
        UserResponseDto userResponseDto = iUserService.updateUser(userUpdateDto, id);

        return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete User",
            description = "Delete a specific user by user id"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatusCode> deleteIssue(@PathVariable("id") UUID id) {
        iUserService.deleteUser(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
