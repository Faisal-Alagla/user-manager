package com.faisal.usermanager.user;

import com.faisal.usermanager.common.exceptions.ErrorMessage;
import com.faisal.usermanager.common.exceptions.InternalServerError;
import com.faisal.usermanager.common.exceptions.ObjectStoreException;
import com.faisal.usermanager.common.exceptions.ResourceException;
import com.faisal.usermanager.common.lookups.LookupService;
import com.faisal.usermanager.common.lookups.LookupStringIdResponseDto;
import com.faisal.usermanager.integration.keyclock.KeycloakService;
import com.faisal.usermanager.integration.objectstore.IObjectStoreService;
import com.faisal.usermanager.integration.objectstore.ObjectMetadata;
import com.faisal.usermanager.integration.objectstore.ObjectOperationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final IObjectStoreService objectStoreService;
    private final KeycloakService keycloakService;
    private final LookupService lookupService;

    @Value("${user-manager.object-store.profile-images-prefix:profile-images}")
    private String profileImagesPrefix;

    @Override
    @Transactional
    public UserResponseDto createUser(UserCreationDto userCreationDto) {
        LookupStringIdResponseDto role = lookupService.findRoleLookupById(userCreationDto.getRoleId())
                .orElseThrow(() -> new ResourceException(ErrorMessage.USER_ROLE_NOT_FOUND));

        User newUser = UserMapper.mapToUser(userCreationDto);
        User createdUser = userRepository.save(newUser);

        String keycloakUserId;
        try {
            keycloakUserId = keycloakService.createUser(
                    createdUser.getId().toString(),
                    createdUser.getEmail(),
                    createdUser.getFirstName(),
                    createdUser.getLastName(),
                    role
            );
        } catch (Exception e) {

            // Rollback
            log.error("Error creating user in keycloak: {}", e.getMessage());
            userRepository.delete(createdUser);
            throw new InternalServerError(ErrorMessage.UNKNOWN_FEIGN_ERROR);
        }

        MultipartFile profileImage = userCreationDto.getProfileImage();
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                String profileImagePath = uploadProfileImage(profileImage, createdUser.getId());
                createdUser.setProfileImageUrl(profileImagePath);
            } catch (ObjectStoreException e) {

                // Rollback
                try {
                    keycloakService.deleteUser(keycloakUserId);
                } catch (Exception cleanupError) {
                    log.error("Failed to cleanup Keycloak user after object store failure", cleanupError);
                }

                userRepository.delete(createdUser);
                throw e;
            }
        }

        createdUser.setIsActive(true);
        createdUser = userRepository.save(createdUser);

        return mapToUserResponseDtoWithBase64Image(createdUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUser(UUID userId) {
        User user = userRepository.findByIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new ResourceException(ErrorMessage.USER_NOT_FOUND));

        return mapToUserResponseDtoWithBase64Image(user);
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(UserUpdateDto userUpdateDto, UUID userId) {
        User user = userRepository.findByIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new ResourceException(ErrorMessage.USER_NOT_FOUND));

        MultipartFile profileImage = userUpdateDto.getProfileImage();
        if (profileImage != null && !profileImage.isEmpty()) {
            // Delete existing profile image if exists
            if (StringUtils.hasText(user.getProfileImageUrl())) {
                deleteProfileImage(user.getProfileImageUrl());
            }

            // Upload the new profile image
            String newProfileImagePath = uploadProfileImage(profileImage, userId);
            user.setProfileImageUrl(newProfileImagePath);
        }

        user.setUpdatedAt(LocalDateTime.now());
        user.setEmail(userUpdateDto.getEmail()); //TODO: make sure to update in keycloak later
        user.setPhone(userUpdateDto.getPhone());
        user.setRoleId(userUpdateDto.getRoleId()); //TODO: check role comparison (higher can change lower)

        User updatedUser = userRepository.save(user);
        return mapToUserResponseDtoWithBase64Image(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID userId) {
        User user = userRepository.findByIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new ResourceException(ErrorMessage.USER_NOT_FOUND));

        if (StringUtils.hasText(user.getProfileImageUrl())) {
            deleteProfileImage(user.getProfileImageUrl());
        }

        userRepository.deactivateUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userExists(UUID userId) {
        return userRepository.findByIdAndIsActiveTrue(userId).isPresent();
    }

    private String uploadProfileImage(MultipartFile file, UUID userId) {
        try {
            String objectPath = generateProfileImagePath(file.getOriginalFilename(), userId);

            ObjectOperationResult<ObjectMetadata> result = objectStoreService.uploadObject(
                    file.getBytes(),
                    objectPath,
                    file.getContentType()
            );

            if (!result.isSuccess()) {
                log.error("Failed to upload profile image: {}", result.getErrorMessage());
                throw new ObjectStoreException(ErrorMessage.OBJECT_STORE_UPLOAD_FAILED);
            }

            return result.getData().getPath();
        } catch (IOException e) {
            log.error("Failed to read profile image content", e);
            throw new ObjectStoreException(ErrorMessage.OBJECT_STORE_UPLOAD_FAILED);
        }
    }

    private void deleteProfileImage(String imagePath) {
        if (!StringUtils.hasText(imagePath)) {
            return;
        }

        ObjectOperationResult<Boolean> result = objectStoreService.deleteObject(imagePath);
        if (!result.isSuccess()) {
            log.error("Failed to delete profile image: {}", result.getErrorMessage());
        }
    }

    private String generateProfileImagePath(String originalFilename, UUID userId) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String sanitizedExtension = extension.replaceAll("[^a-zA-Z0-9.-]", "");
        return String.format("%s/%s%s", profileImagesPrefix, userId, sanitizedExtension);
    }

    private UserResponseDto mapToUserResponseDtoWithBase64Image(User user) {
        UserResponseDto dto = UserMapper.mapToUserResponseDto(user);

        if (StringUtils.hasText(user.getProfileImageUrl())) {
            String base64Image = getProfileImageAsBase64(user.getProfileImageUrl());
            dto.setProfileImageBase64(base64Image);
        }

        return dto;
    }

    private String getProfileImageAsBase64(String imagePath) {
        try {
            ObjectOperationResult<Pair<byte[], String>> result = objectStoreService.getObject(imagePath);

            if (!result.isSuccess()) {
                log.error("Failed to get profile image: {}", result.getErrorMessage());
                return null;
            }

            byte[] imageBytes = result.getData().getFirst();
            String contentType = result.getData().getSecond();

            if (imageBytes.length == 0) {
                return null;
            }

            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            return "data:" + contentType + ";base64," + base64Image;

        } catch (Exception e) {
            log.error("Error encoding profile image to base64", e);
            return null;
        }
    }

}