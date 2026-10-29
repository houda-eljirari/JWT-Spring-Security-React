package ma.enset.incident_management.dto;

public record AuthResponse(
        String token,
        String tokenType,
        String username,
        String role
) {
}
