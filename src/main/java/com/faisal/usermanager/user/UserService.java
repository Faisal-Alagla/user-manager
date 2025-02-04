package com.faisal.usermanager.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserResponseDto createUser(UserCreationDto userCreationDto) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUser(UUID userId) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        return null;
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(UserUpdateDto userUpdateDto, UUID userId) {
        return null;
    }

    @Override
    @Transactional
    public void deleteUser(UUID userId) {

    }

    @Override
    @Transactional(readOnly = true)
    public boolean userExists(UUID userId) {
        return false;
    }

}
