package com.faisal.usermanager.user;

import com.faisal.usermanager.common.exceptions.ErrorMessage;
import com.faisal.usermanager.common.exceptions.ObjectStoreException;
import com.faisal.usermanager.common.exceptions.ResourceException;
import com.faisal.usermanager.integration.objectstore.IObjectStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;

    private final IObjectStoreService objectStoreService;

    @Override
    @Transactional
    public UserResponseDto createUser(UserCreationDto userCreationDto) {

        String profileImagePath = null;
        if (userCreationDto.getProfileImage() != null) {
            try {
                profileImagePath = uploadUserPhoto(userCreationDto.getProfileImage());
            } catch (Exception e) {
                throw new ObjectStoreException(ErrorMessage.OBJECT_STORE_UPLOAD_FAILED);
            }
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
        if (userUpdateDto.getProfileImage() != null) {
            try {
                newProfileImagePath = uploadUserPhoto(userUpdateDto.getProfileImage());
            } catch (Exception e) {
                throw new ObjectStoreException(ErrorMessage.OBJECT_STORE_UPLOAD_FAILED);
            }
        }

        updateUserData(user, userUpdateDto, newProfileImagePath);
        User updatedUser = userRepository.save(user);

        return UserMapper.mapToUserResponseDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID userId) {
        if (!userExists(userId)) {
            throw new ResourceException(ErrorMessage.USER_NOT_FOUND);
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

        if (newProfileImagePath != null && !newProfileImagePath.isEmpty()) {
            user.setProfileImageUrl(newProfileImagePath);
        }
    }

    // Returns the path of the uploaded file
    private String uploadUserPhoto(MultipartFile file) throws IOException {
        String filePath = "user-profile-images/" + UUID.randomUUID();

        objectStoreService.uploadObject(
                file.getBytes(),
                filePath,
                file.getContentType()
        );

        return filePath;
    }

}
