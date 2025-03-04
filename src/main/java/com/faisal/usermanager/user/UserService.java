package com.faisal.usermanager.user;

import com.faisal.usermanager.common.exceptions.ErrorMessage;
import com.faisal.usermanager.common.exceptions.ObjectStoreException;
import com.faisal.usermanager.common.exceptions.ResourceException;
import com.faisal.usermanager.integration.objectstore.IObjectStoreService;
import com.faisal.usermanager.integration.objectstore.ObjectMetadata;
import com.faisal.usermanager.integration.objectstore.ObjectOperationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final IObjectStoreService objectStoreService;

    @Value("${user-manager.object-store.profile-images-prefix:profile-images}")
    private String profileImagesPrefix;

    @Override
    @Transactional
    public UserResponseDto createUser(UserCreationDto userCreationDto) {
        String profileImagePath = null;
        MultipartFile profileImage = userCreationDto.getProfileImage();

        if (profileImage != null && !profileImage.isEmpty()) {
            profileImagePath = uploadProfileImage(profileImage);
        }

        User newUser = UserMapper.mapToUser(userCreationDto);
        newUser.setProfileImageUrl(profileImagePath);
        User createdUser = userRepository.save(newUser);

        return UserMapper.mapToUserResponseDto(createdUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUser(UUID userId) {
        User user = userRepository.findByIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new ResourceException(ErrorMessage.USER_NOT_FOUND));

        return UserMapper.mapToUserResponseDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        return userRepository.findByIsActiveTrue(pageable).map(UserMapper::mapToUserResponseDto);
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(UserUpdateDto userUpdateDto, UUID userId) {
        User user = userRepository.findByIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new ResourceException(ErrorMessage.USER_NOT_FOUND));

        String newProfileImagePath = null;
        MultipartFile profileImage = userUpdateDto.getProfileImage();

        if (profileImage != null && !profileImage.isEmpty()) {
            // Delete existing profile image if there is one
            if (StringUtils.hasText(user.getProfileImageUrl())) {
                deleteProfileImage(user.getProfileImageUrl());
            }

            // Upload the new profile image
            newProfileImagePath = uploadProfileImage(profileImage);
        }

        updateUserData(user, userUpdateDto, newProfileImagePath);
        User updatedUser = userRepository.save(user);

        return UserMapper.mapToUserResponseDto(updatedUser);
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

    private void updateUserData(User user, UserUpdateDto userUpdateDto, String newProfileImagePath) {
        user.setEmail(userUpdateDto.getEmail()); //TODO: make sure to update in keycloak later
        user.setPhone(userUpdateDto.getPhone());
        user.setRoleId(userUpdateDto.getRoleId()); //TODO: check role comparison (higher can change lower)

        if (StringUtils.hasText(newProfileImagePath)) {
            user.setProfileImageUrl(newProfileImagePath);
        }
    }

    private String uploadProfileImage(MultipartFile file) {
        try {
            String objectPath = generateProfileImagePath(file.getOriginalFilename());

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
            log.warn("Failed to delete profile image: {}", result.getErrorMessage());
        }
    }

    private String generateProfileImagePath(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String sanitizedExtension = extension.replaceAll("[^a-zA-Z0-9.-]", "");
        return String.format("%s/%s%s", profileImagesPrefix, UUID.randomUUID(), sanitizedExtension);
    }

}