package org.bkv.orders.services.impls;

import org.bkv.orders.dto.requests.RegisterUserRequest;
import org.bkv.orders.entity.UserEntity;
import org.bkv.orders.mappers.UserMapper;
import org.bkv.orders.models.LoginResult;
import org.bkv.orders.models.UserDto;
import org.bkv.orders.repo.UserRepository;
import org.bkv.orders.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_success_returnsLoginResult() {
        String username = "test";
        String password = "pass";

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtUtil.generateAccessToken(username)).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(username)).thenReturn("refresh-token");

        UserEntity userEntity = new UserEntity();
        UserDto userDto = new UserDto(1L, "test", "pass", "user");
        when(userRepository.findByUserName(username)).thenReturn(Optional.of(userEntity));
        when(userMapper.toUserDto(userEntity)).thenReturn(userDto);

        LoginResult result = userService.login(username, password);

        assertNotNull(result);
        assertEquals("access-token", result.accessToken());
        assertEquals("refresh-token", result.refreshToken());
        assertEquals(userDto, result.user());
    }

    @Test
    void saveUser_callsRepositorySave() {
        UserEntity user = new UserEntity();
        when(userRepository.save(user)).thenReturn(user);

        UserEntity saved = userService.saveUser(user);

        assertEquals(user, saved);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void findByUserName_callsRepository() {
        UserEntity user = new UserEntity();
        when(userRepository.findByUserName("test")).thenReturn(Optional.of(user));

        Optional<UserEntity> result = userService.findByUserName("test");

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void saveAndGetUserEntity_userAlreadyPresent_returnsNull() {
        RegisterUserRequest request = mock(RegisterUserRequest.class);
        when(request.userName()).thenReturn("test");
        when(userRepository.findByUserName("test")).thenReturn(Optional.of(new UserEntity()));

        UserEntity result = userService.saveAndGetUserEntity(request);

        assertNull(result);
    }

    @Test
    void saveAndGetUserEntity_userNotPresent_savesUser() {
        RegisterUserRequest request = mock(RegisterUserRequest.class);
        when(request.userName()).thenReturn("test");

        when(userRepository.findByUserName("test")).thenReturn(Optional.empty());

        UserEntity entity = new UserEntity();
        when(userMapper.toUserEntity(request, passwordEncoder)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(entity);

        UserEntity result = userService.saveAndGetUserEntity(request);

        assertEquals(entity, result);
        verify(userRepository, times(1)).save(entity);
    }

    @Test
    void checkRefreshToken_callsJwtUtil() {
        when(jwtUtil.isRefreshTokenValid("token")).thenReturn(true);

        boolean result = userService.checkRefreshToken("token");

        assertTrue(result);
    }

    @Test
    void createRefreshToken_extractsUsernameAndGeneratesAccessToken() {
        when(jwtUtil.extractUserName("refresh-token")).thenReturn("user");
        when(jwtUtil.generateAccessToken("user")).thenReturn("new-access-token");

        String result = userService.createRefreshToken("refresh-token");

        assertEquals("new-access-token", result);
    }
}