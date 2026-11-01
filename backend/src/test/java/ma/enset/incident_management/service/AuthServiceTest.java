package ma.enset.incident_management.service;

import ma.enset.incident_management.dto.AuthResponse;
import ma.enset.incident_management.dto.LoginRequest;
import ma.enset.incident_management.dto.RegisterRequest;
import ma.enset.incident_management.entity.Role;
import ma.enset.incident_management.entity.User;
import ma.enset.incident_management.repository.UserRepository;
import ma.enset.incident_management.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest(
                "houda", "houda@example.com", "password123");

        savedUser = new User();
        savedUser.setUsername("houda");
        savedUser.setEmail("houda@example.com");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole(Role.EMPLOYEE);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        when(userRepository.existsByUsername("houda")).thenReturn(false);
        when(userRepository.existsByEmail("houda@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt-token");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.token());
        assertEquals("Bearer", response.tokenType());
        assertEquals("houda", response.username());
        assertEquals("EMPLOYEE", response.role());

        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(any(UserDetails.class));
    }

    @Test
    void shouldRejectExistingUsername() {
        when(userRepository.existsByUsername("houda")).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.register(registerRequest));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldRejectExistingEmail() {
        when(userRepository.existsByUsername("houda")).thenReturn(false);
        when(userRepository.existsByEmail("houda@example.com")).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.register(registerRequest));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest("houda", "password123");

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("houda")
                .password("encodedPassword")
                .roles("EMPLOYEE")
                .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

        when(authenticationManager.authenticate(any(
                UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");
        when(userRepository.findByUsername("houda")).thenReturn(Optional.of(savedUser));

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.token());
        assertEquals("houda", response.username());
        assertEquals("EMPLOYEE", response.role());

        verify(authenticationManager).authenticate(any(
                UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void shouldRejectLoginWhenUserIsNotFound() {
        LoginRequest request = new LoginRequest("unknown", "password123");

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("unknown")
                .password("encodedPassword")
                .roles("EMPLOYEE")
                .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

        when(authenticationManager.authenticate(any(
                UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.login(request));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }
}
