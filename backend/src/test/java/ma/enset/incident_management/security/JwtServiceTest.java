package ma.enset.incident_management.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        String testSecret =
                "VGhpc0lzQUxvY2FsVGVzdFNlY3JldEtleUZvckpXVFRlc3RzMjAyNg==";

        jwtService = new JwtService(testSecret);

        userDetails = User.withUsername("houda")
                .password("password")
                .authorities("ROLE_EMPLOYEE")
                .build();
    }

    @Test
    void shouldGenerateToken() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldExtractUsernameFromToken() {
        String token = jwtService.generateToken(userDetails);

        String username = jwtService.extractUsername(token);

        assertEquals("houda", username);
    }

    @Test
    void shouldValidateTokenForCorrectUser() {
        String token = jwtService.generateToken(userDetails);

        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void shouldRejectTokenForDifferentUser() {
        String token = jwtService.generateToken(userDetails);

        UserDetails anotherUser = User.withUsername("anotherUser")
                .password("password")
                .authorities("ROLE_EMPLOYEE")
                .build();

        boolean isValid = jwtService.isTokenValid(token, anotherUser);

        assertFalse(isValid);
    }

    @Test
    void shouldRejectTamperedToken() {
        String token = jwtService.generateToken(userDetails);

        int lastIndex = token.length() - 1;
        char replacement = token.charAt(lastIndex) == 'a' ? 'b' : 'a';

        String tamperedToken = token.substring(0, lastIndex) + replacement;

        assertFalse(jwtService.isTokenValid(tamperedToken, userDetails));
    }
}
