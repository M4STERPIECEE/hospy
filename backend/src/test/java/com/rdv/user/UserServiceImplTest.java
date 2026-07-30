package com.rdv.user;

import com.rdv.user.dto.UserRequest;
import com.rdv.user.dto.UserResponse;
import com.rdv.user.exception.DuplicateEmailException;
import com.rdv.user.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User sampleUser;
    private UserResponse sampleResponse;
    private UserRequest sampleRequest;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        sampleUser = User.builder()
                .id(userId)
                .firstName("Jean")
                .lastName("Dupont")
                .email("jean.dupont@example.com")
                .role(UserRole.USER)
                .build();

        sampleResponse = new UserResponse(userId, "Jean", "Dupont", "jean.dupont@example.com", "0102030405", UserRole.USER, null);
        sampleRequest = new UserRequest("Jean", "Dupont", "jean.dupont@example.com", "0102030405", "password", UserRole.USER);
    }

    @Test
    void getById_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
        when(userMapper.toResponse(sampleUser)).thenReturn(sampleResponse);

        UserResponse response = userService.getById(userId);

        assertNotNull(response);
        assertEquals(userId, response.id());
        verify(userRepository).findById(userId);
    }

    @Test
    void getById_NotFound_ThrowsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getById(userId));
    }

    @Test
    void create_DuplicateEmail_ThrowsException() {
        when(userRepository.existsByEmail(sampleRequest.email())).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> userService.create(sampleRequest));
    }

    @Test
    void create_Success() {
        when(userRepository.existsByEmail(sampleRequest.email())).thenReturn(false);
        when(userMapper.toEntity(sampleRequest)).thenReturn(sampleUser);
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);
        when(userMapper.toResponse(sampleUser)).thenReturn(sampleResponse);

        UserResponse response = userService.create(sampleRequest);

        assertNotNull(response);
        assertEquals("jean.dupont@example.com", response.email());
    }
}
